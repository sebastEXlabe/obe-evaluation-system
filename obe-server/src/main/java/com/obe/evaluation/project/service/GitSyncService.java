package com.obe.evaluation.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.project.entity.GitCommitLog;
import com.obe.evaluation.project.entity.RepoConfig;
import com.obe.evaluation.project.mapper.GitCommitLogMapper;
import com.obe.evaluation.project.mapper.RepoConfigMapper;
import com.obe.evaluation.system.entity.SysUser;
import com.obe.evaluation.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitSyncService {

    private final RepoConfigMapper repoConfigMapper;
    private final GitCommitLogMapper commitLogMapper;
    private final SysUserMapper userMapper;

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(30000);
        return new RestTemplate(factory);
    }

    @Transactional
    public Map<String, Object> syncByGroupId(Long groupId) {
        List<RepoConfig> repos = repoConfigMapper.selectList(
                new LambdaQueryWrapper<RepoConfig>().eq(RepoConfig::getGroupId, groupId));
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("groupId", groupId);
        summary.put("totalRepos", repos.size());
        int totalCommits = 0;
        int syncedRepos = 0;
        List<String> errors = new ArrayList<>();

        for (RepoConfig repo : repos) {
            if (repo.getEnabled() == null || !repo.getEnabled()) {
                continue;
            }
            try {
                int count = syncSingleRepo(repo);
                totalCommits += count;
                syncedRepos++;
                repo.setLastSyncedAt(LocalDateTime.now());
                repoConfigMapper.updateById(repo);
            } catch (Exception e) {
                log.error("Git sync failed for repo {} (groupId={}): {}", repo.getRepo(), groupId, e.getMessage());
                errors.add(repo.getRepo() + ": " + e.getMessage());
            }
        }

        summary.put("syncedRepos", syncedRepos);
        summary.put("totalNewCommits", totalCommits);
        summary.put("errors", errors);
        return summary;
    }

    @Transactional
    public Map<String, Object> syncAllEnabled() {
        List<RepoConfig> repos = repoConfigMapper.selectList(
                new LambdaQueryWrapper<RepoConfig>().eq(RepoConfig::getEnabled, true));
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalRepos", repos.size());
        int totalCommits = 0;
        int syncedRepos = 0;
        List<String> errors = new ArrayList<>();

        for (RepoConfig repo : repos) {
            try {
                int count = syncSingleRepo(repo);
                if (count > 0) {
                    totalCommits += count;
                    syncedRepos++;
                    repo.setLastSyncedAt(LocalDateTime.now());
                    repoConfigMapper.updateById(repo);
                }
            } catch (Exception e) {
                log.error("Git sync failed for repo {}: {}", repo.getRepo(), e.getMessage());
                errors.add(repo.getRepo() + ": " + e.getMessage());
            }
        }

        summary.put("syncedRepos", syncedRepos);
        summary.put("totalNewCommits", totalCommits);
        summary.put("errors", errors);
        return summary;
    }

    @SuppressWarnings("unchecked")
    private int syncSingleRepo(RepoConfig repo) {
        RestTemplate restTemplate = createRestTemplate();
        String platform = repo.getPlatform() != null ? repo.getPlatform().toLowerCase() : "github";
        String owner = repo.getOwner();
        String repoName = repo.getRepo();
        String branch = repo.getBranch() != null ? repo.getBranch() : "main";

        // 首次同步不设since（拉全部），后续增量同步
        LocalDateTime since = repo.getLastSyncedAt();

        List<Map<String, Object>> commits;
        if ("gitee".equals(platform)) {
            commits = fetchGiteeCommits(restTemplate, repo, owner, repoName, branch, since);
        } else {
            commits = fetchGitHubCommits(restTemplate, repo, owner, repoName, branch, since);
        }
        log.info("Git sync: {} returned {} commits for {}/{}", platform, commits.size(), owner, repoName);

        int newCount = 0;
        log.info("Processing {} commits for groupId={}", commits.size(), repo.getGroupId());
        for (Map<String, Object> c : commits) {
            String commitHash = (String) c.getOrDefault("sha", "");
            if (commitHash.isEmpty()) { log.info("Skipping empty sha"); continue; }

            Long existingCount = commitLogMapper.selectCount(
                    new LambdaQueryWrapper<GitCommitLog>()
                            .eq(GitCommitLog::getCommitHash, commitHash)
                            .eq(GitCommitLog::getGroupId, repo.getGroupId()));
            if (existingCount > 0) { log.info("Skipping duplicate: {}", commitHash); continue; }

            Map<String, Object> authorMap = (Map<String, Object>) c.getOrDefault("author", Map.of());
            String authorEmail = "";
            String authorName = "";
            if (authorMap != null) {
                authorEmail = (String) authorMap.getOrDefault("email", "");
                authorName = (String) authorMap.getOrDefault("name", "");
            }
            log.info("Commit {}: author={} <{}>", commitHash.substring(0, Math.min(7, commitHash.length())), authorName, authorEmail);

            Long userId = matchUser(authorEmail, authorName);
            if (userId == null) {
                log.info("No user match for {} <{}>, skipping", authorName, authorEmail);
                continue;
            }
            log.info("Matched to userId={}", userId);

            GitCommitLog logEntry = new GitCommitLog();
            logEntry.setUserId(userId);
            logEntry.setGroupId(repo.getGroupId());
            logEntry.setRepoName(repoName);
            logEntry.setCommitHash(commitHash);
            logEntry.setMessage((String) c.getOrDefault("message", ""));
            logEntry.setAdditions(c.get("additions") != null ? ((Number) c.get("additions")).intValue() : 0);
            logEntry.setDeletions(c.get("deletions") != null ? ((Number) c.get("deletions")).intValue() : 0);
            logEntry.setCommittedAt(parseCommitDate(c.get("committed_at")));
            logEntry.setSyncedAt(LocalDateTime.now());

            if (commitLogMapper.insert(logEntry) > 0) newCount++;
        }
        return newCount;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchGitHubCommits(RestTemplate rest, RepoConfig repo,
                                                          String owner, String repoName,
                                                          String branch, LocalDateTime since) {
        String url = "https://api.github.com/repos/" + owner + "/" + repoName
                + "/commits?sha=" + branch + "&per_page=100";
        if (since != null) {
            url += "&since=" + since.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (repo.getAccessToken() != null && !repo.getAccessToken().isEmpty()) {
            headers.setBearerAuth(repo.getAccessToken());
        }
        headers.set("User-Agent", "OBE-Evaluation-System");

        try {
            log.info("Git fetch: {}", url);
            ResponseEntity<String> response = rest.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            if (response.getBody() == null) { log.info("Git fetch: empty body"); return List.of(); }

            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            List<Map<String, Object>> rawList = om.readValue(response.getBody(), List.class);
            log.info("Git fetch OK: {} items from body length={}", rawList.size(), response.getBody().length());

            List<Map<String, Object>> result = new ArrayList<>();
            for (Object item : rawList) {
                Map<String, Object> commitData = (Map<String, Object>) item;
                Map<String, Object> commit = (Map<String, Object>) commitData.getOrDefault("commit", Map.of());
                Map<String, Object> author = (Map<String, Object>) commit.getOrDefault("author", Map.of());

                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("sha", commitData.getOrDefault("sha", ""));
                entry.put("message", commit.getOrDefault("message", ""));
                entry.put("author", author);
                entry.put("committed_at", author.getOrDefault("date", ""));
                entry.put("additions", 0);
                entry.put("deletions", 0);
                result.add(entry);
            }

            for (Map<String, Object> entry : result) {
                String sha = (String) entry.get("sha");
                if (sha == null || sha.isEmpty()) continue;
                String detailUrl = "https://api.github.com/repos/" + owner + "/" + repoName + "/commits/" + sha;
                try {
                    ResponseEntity<Map> detailResp = rest.exchange(
                            detailUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
                    if (detailResp.getBody() != null) {
                        Map<String, Object> stats = (Map<String, Object>) detailResp.getBody().get("stats");
                        if (stats != null) {
                            entry.put("additions", stats.getOrDefault("additions", 0));
                            entry.put("deletions", stats.getOrDefault("deletions", 0));
                        }
                    }
                } catch (Exception e) {
                    log.debug("Failed to fetch commit detail for {}: {}", sha, e.getMessage());
                }
            }

            return result;
        } catch (Exception e) {
            log.error("GitHub API error: {}", e.getMessage());
            throw new RuntimeException("GitHub同步失败: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchGiteeCommits(RestTemplate rest, RepoConfig repo,
                                                         String owner, String repoName,
                                                         String branch, LocalDateTime since) {
        String url = "https://gitee.com/api/v5/repos/" + owner + "/" + repoName
                + "/commits?sha=" + branch + "&per_page=100";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (repo.getAccessToken() != null && !repo.getAccessToken().isEmpty()) {
            url += "&access_token=" + repo.getAccessToken();
        }
        headers.set("User-Agent", "OBE-Evaluation-System");

        try {
            log.info("Git fetch: {}", url);
            ResponseEntity<String> response = rest.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            if (response.getBody() == null) { log.info("Git fetch: empty body"); return List.of(); }

            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            List<Map<String, Object>> rawList = om.readValue(response.getBody(), List.class);
            log.info("Git fetch OK: {} items from body length={}", rawList.size(), response.getBody().length());

            List<Map<String, Object>> result = new ArrayList<>();
            for (Object item : rawList) {
                Map<String, Object> commitData = (Map<String, Object>) item;
                Map<String, Object> commit = (Map<String, Object>) commitData.getOrDefault("commit", Map.of());
                Map<String, Object> author = (Map<String, Object>) commit.getOrDefault("author", Map.of());
                // Read stats from Gitee V5 API response
                Map<String, Object> stats = (Map<String, Object>) commitData.getOrDefault("stats", Map.of());
                int additions = stats.get("additions") != null ? ((Number) stats.get("additions")).intValue() : 0;
                int deletions = stats.get("deletions") != null ? ((Number) stats.get("deletions")).intValue() : 0;

                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("sha", commitData.getOrDefault("sha", ""));
                entry.put("message", commit.getOrDefault("message", ""));
                entry.put("author", author);
                entry.put("committed_at", author.getOrDefault("date", ""));
                entry.put("additions", additions);
                entry.put("deletions", deletions);
                result.add(entry);
            }
            return result;
        } catch (Exception e) {
            log.error("Gitee API error: {}", e.getMessage());
            throw new RuntimeException("Gitee同步失败: " + e.getMessage());
        }
    }

    private Long matchUser(String email, String name) {
        // 1. 按Git邮箱匹配
        if (email != null && !email.isEmpty()) {
            List<SysUser> users = userMapper.selectList(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getGitEmail, email));
            if (!users.isEmpty()) return users.get(0).getId();
        }
        // 2. 按Git用户名匹配
        if (name != null && !name.isEmpty()) {
            List<SysUser> users = userMapper.selectList(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getGitUsername, name));
            if (!users.isEmpty()) return users.get(0).getId();
        }
        // 3. 无匹配 → 默认归到管理员(admin id=1)，不跳过提交
        return 1L;
    }

    private LocalDateTime parseCommitDate(Object dateObj) {
        if (dateObj == null) return LocalDateTime.now();
        try {
            String dateStr = dateObj.toString();
            ZonedDateTime zdt = ZonedDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
            return zdt.toLocalDateTime();
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
// fixme: cleanup
