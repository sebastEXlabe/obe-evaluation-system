package com.obe.evaluation.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.common.R;
import com.obe.evaluation.project.entity.RepoConfig;
import com.obe.evaluation.project.mapper.RepoConfigMapper;
import com.obe.evaluation.project.service.GitSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/git-sync")
@RequiredArgsConstructor
@Tag(name = "Git同步管理")
public class GitSyncController {

    private final RepoConfigMapper repoConfigMapper;
    private final GitSyncService gitSyncService;

    private boolean isTeacherOrAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TEACHER"));
    }

    // ========== 仓库配置 CRUD ==========

    @GetMapping("/repos")
    @Operation(summary = "查询仓库配置列表")
    public R<List<RepoConfig>> listRepos(@RequestParam(required = false) Long groupId) {
        var wq = new LambdaQueryWrapper<RepoConfig>();
        if (groupId != null) wq.eq(RepoConfig::getGroupId, groupId);
        return R.ok(repoConfigMapper.selectList(wq));
    }

    @GetMapping("/repos/{id}")
    @Operation(summary = "查询单个仓库配置")
    public R<RepoConfig> getRepo(@PathVariable Long id) {
        RepoConfig config = repoConfigMapper.selectById(id);
        if (config != null) config.setAccessToken(null);
        return R.ok(config);
    }

    @PostMapping("/repos")
    @Operation(summary = "创建仓库配置")
    public R<RepoConfig> createRepo(@RequestBody RepoConfig config) {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        if (config.getBranch() == null || config.getBranch().isEmpty()) {
            config.setBranch("main");
        }
        if (config.getEnabled() == null) {
            config.setEnabled(true);
        }
        repoConfigMapper.insert(config);
        config.setAccessToken(null);
        return R.ok(config);
    }

    @PutMapping("/repos")
    @Operation(summary = "更新仓库配置")
    public R<RepoConfig> updateRepo(@RequestBody RepoConfig config) {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        RepoConfig existing = repoConfigMapper.selectById(config.getId());
        if (existing == null) return R.fail(404, "仓库配置不存在");
        if (config.getAccessToken() == null || config.getAccessToken().isEmpty()) {
            config.setAccessToken(existing.getAccessToken());
        }
        repoConfigMapper.updateById(config);
        config.setAccessToken(null);
        return R.ok(config);
    }

    @DeleteMapping("/repos/{id}")
    @Operation(summary = "删除仓库配置")
    public R<Void> deleteRepo(@PathVariable Long id) {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        repoConfigMapper.deleteById(id);
        return R.ok();
    }

    // ========== 同步操作 ==========

    @PostMapping("/sync/{groupId}")
    @Operation(summary = "触发指定小组的Git同步")
    public R<Map<String, Object>> syncGroup(@PathVariable Long groupId) {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        Map<String, Object> result = gitSyncService.syncByGroupId(groupId);
        return R.ok(result);
    }

    @PostMapping("/sync-all")
    @Operation(summary = "触发所有已启用仓库的Git同步")
    public R<Map<String, Object>> syncAll() {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        Map<String, Object> result = gitSyncService.syncAllEnabled();
        return R.ok(result);
    }

    @GetMapping("/status/{groupId}")
    @Operation(summary = "查看小组同步状态")
    public R<Map<String, Object>> status(@PathVariable Long groupId) {
        List<RepoConfig> repos = repoConfigMapper.selectList(
                new LambdaQueryWrapper<RepoConfig>().eq(RepoConfig::getGroupId, groupId));
        return R.ok(Map.of("repos", repos, "totalRepos", repos.size(),
                "enabledCount", repos.stream().filter(r -> r.getEnabled() != null && r.getEnabled()).count()));
    }
}
