package com.obe.evaluation.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.obe.evaluation.common.PageQuery;
import com.obe.evaluation.common.R;
import com.obe.evaluation.project.entity.*;
import com.obe.evaluation.project.mapper.*;
import com.obe.evaluation.system.mapper.SysUserMapper;
import com.obe.evaluation.project.service.GitSyncService;
import com.obe.evaluation.group.entity.GroupMember;
import com.obe.evaluation.group.mapper.GroupMemberMapper;
import com.obe.evaluation.system.mapper.NotificationMapper;
import com.obe.evaluation.system.mapper.SysUserMapper;
import com.obe.evaluation.system.entity.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
@Tag(name = "项目管理")
public class ProjectController {

    private final ProjectMilestoneMapper milestoneMapper;
    private final ProjectTaskMapper taskMapper;
    private final ProjectJournalMapper journalMapper;
    private final GitCommitLogMapper gitCommitMapper;
    private final ContributionLogMapper contributionMapper;
    private final SysUserMapper userMapper;
    private final RepoConfigMapper repoConfigMapper;
    private final GitSyncService gitSyncService;
    private final RequirementChangeMapper reqChangeMapper;
    private final NotificationMapper notificationMapper;
    private final GroupMemberMapper groupMemberMapper;

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long))
            throw new IllegalArgumentException("未登录");
        return (Long) auth.getPrincipal();
    }

    private boolean isTeacherOrAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TEACHER"));
    }

    /** 学生角色时返回其所在小组ID列表，用于数据隔离 */
    private List<Long> getStudentGroupIds() {
        if (isTeacherOrAdmin()) return List.of(); // 教师/管理员不需要限制
        Long userId = currentUserId();
        return groupMemberMapper.selectList(
            new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getUserId, userId))
            .stream().map(GroupMember::getGroupId).toList();
    }

    // ========== 里程碑 ==========

    @GetMapping("/milestones")
    @Operation(summary = "查询里程碑列表（学生仅看自己小组）")
    public R<Page<ProjectMilestone>> listMilestones(@RequestParam Long groupId, PageQuery q) {
        List<Long> studentGroupIds = getStudentGroupIds();
        if (!studentGroupIds.isEmpty() && !studentGroupIds.contains(groupId))
            return R.fail(403, "无权查看该小组数据");
        var wq = new LambdaQueryWrapper<ProjectMilestone>()
                .eq(ProjectMilestone::getGroupId, groupId)
                .orderByAsc(ProjectMilestone::getCreatedAt);
        return R.ok(milestoneMapper.selectPage(new Page<>(q.getPage(), q.getSize()), wq));
    }

    @PostMapping("/milestones")
    @Operation(summary = "创建里程碑")
    public R<ProjectMilestone> createMilestone(@RequestBody ProjectMilestone m) {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        milestoneMapper.insert(m);
        // Notify group members
        try {
            var members = groupMemberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>()
                    .eq(GroupMember::getGroupId, m.getGroupId()));
            for (var member : members) {
                Notification n = new Notification();
                n.setUserId(member.getUserId());
                n.setTitle("新里程碑");
                n.setContent("项目新增里程碑: " + (m.getTitle() != null ? m.getTitle() : "未命名"));
                n.setIsRead(false);
                n.setCreatedAt(LocalDateTime.now());
                notificationMapper.insert(n);
            }
        } catch (Exception e) {
            log.warn("创建里程碑通知失败: {}", e.getMessage());
        }
        return R.ok(m);
    }

    @PutMapping("/milestones/{id}")
    @Operation(summary = "更新里程碑")
    public R<ProjectMilestone> updateMilestone(@PathVariable Long id, @RequestBody ProjectMilestone m) {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        m.setId(id);
        if (m.getIsDone() != null && m.getIsDone()) {
            m.setFinishedAt(LocalDateTime.now());
        }
        milestoneMapper.updateById(m);
        return R.ok(m);
    }

    @DeleteMapping("/milestones/{id}")
    @Operation(summary = "删除里程碑")
    public R<Void> deleteMilestone(@PathVariable Long id) {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        milestoneMapper.deleteById(id);
        return R.ok();
    }

    // ========== 任务 ==========

    @GetMapping("/tasks")
    @Operation(summary = "查询任务列表（学生仅看自己小组）")
    public R<Page<ProjectTask>> listTasks(@RequestParam(required = false) Long groupId,
                                           @RequestParam(required = false) Long milestoneId,
                                           PageQuery q) {
        var wq = new LambdaQueryWrapper<ProjectTask>();
        List<Long> studentGroupIds = getStudentGroupIds();
        if (!studentGroupIds.isEmpty()) {
            wq.in(ProjectTask::getGroupId, studentGroupIds);
        } else if (groupId != null) {
            wq.eq(ProjectTask::getGroupId, groupId);
        }
        if (milestoneId != null) wq.eq(ProjectTask::getMilestoneId, milestoneId);
        wq.orderByDesc(ProjectTask::getCreatedAt);
        return R.ok(taskMapper.selectPage(new Page<>(q.getPage(), q.getSize()), wq));
    }

    @PostMapping("/tasks")
    @Operation(summary = "创建任务")
    public R<ProjectTask> createTask(@RequestBody ProjectTask t) {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        taskMapper.insert(t);
        // Notify relevant users (assignee if set, else group members)
        try {
            if (t.getAssigneeId() != null) {
                Notification n = new Notification();
                n.setUserId(t.getAssigneeId());
                n.setTitle("新任务分配");
                n.setContent("您被分配了新任务: " + (t.getTitle() != null ? t.getTitle() : "未命名"));
                n.setIsRead(false);
                n.setCreatedAt(LocalDateTime.now());
                notificationMapper.insert(n);
            } else if (t.getGroupId() != null) {
                var members = groupMemberMapper.selectList(
                    new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, t.getGroupId()));
                for (var member : members) {
                    Notification n = new Notification();
                    n.setUserId(member.getUserId());
                    n.setTitle("新任务");
                    n.setContent("项目新增任务: " + (t.getTitle() != null ? t.getTitle() : "未命名"));
                    n.setIsRead(false);
                    n.setCreatedAt(LocalDateTime.now());
                    notificationMapper.insert(n);
                }
            }
        } catch (Exception e) {
            log.warn("创建任务通知失败: {}", e.getMessage());
        }
        return R.ok(t);
    }

    @PutMapping("/tasks/{id}")
    @Operation(summary = "更新任务")
    public R<ProjectTask> updateTask(@PathVariable Long id, @RequestBody ProjectTask t) {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        t.setId(id);
        taskMapper.updateById(t);
        return R.ok(t);
    }

    @DeleteMapping("/tasks/{id}")
    @Operation(summary = "删除任务")
    public R<Void> deleteTask(@PathVariable Long id) {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        taskMapper.deleteById(id);
        return R.ok();
    }

    // ========== 日志 ==========

    @GetMapping("/journals")
    @Operation(summary = "查询项目日志（学生仅看自己小组，已解析姓名）")
    public R<Map<String, Object>> listJournals(@RequestParam(required = false) Long groupId,
                                                 @RequestParam(required = false) Long userId,
                                                 PageQuery q) {
        var wq = new LambdaQueryWrapper<ProjectJournal>();
        List<Long> studentGroupIds = getStudentGroupIds();
        if (!studentGroupIds.isEmpty()) {
            wq.in(ProjectJournal::getGroupId, studentGroupIds);
        } else if (groupId != null) {
            wq.eq(ProjectJournal::getGroupId, groupId);
        }
        if (userId != null) wq.eq(ProjectJournal::getUserId, userId);
        wq.orderByDesc(ProjectJournal::getCreatedAt);
        Page<ProjectJournal> page = journalMapper.selectPage(new Page<>(q.getPage(), q.getSize()), wq);
        List<Map<String, Object>> enriched = page.getRecords().stream().map(j -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", j.getId()); m.put("groupId", j.getGroupId()); m.put("userId", j.getUserId());
            m.put("content", j.getContent()); m.put("journalDate", j.getJournalDate());
            m.put("createdAt", j.getCreatedAt());
            var u = userMapper.selectById(j.getUserId());
            m.put("userName", u != null ? u.getRealName() : "");
            m.put("realName", u != null ? u.getRealName() : "");
            return m;
        }).toList();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", enriched); result.put("total", page.getTotal());
        result.put("size", page.getSize()); result.put("current", page.getCurrent()); result.put("pages", page.getPages());
        return R.ok(result);
    }

    @PostMapping("/journals")
    @Operation(summary = "写日志")
    public R<ProjectJournal> createJournal(@RequestBody ProjectJournal j) {
        j.setUserId(currentUserId());
        if (j.getJournalDate() == null) {
            j.setJournalDate(LocalDate.now());
        }
        journalMapper.insert(j);
        return R.ok(j);
    }

    @PutMapping("/journals/{id}")
    @Operation(summary = "更新日志")
    public R<ProjectJournal> updateJournal(@PathVariable Long id, @RequestBody ProjectJournal j) {
        j.setId(id);
        ProjectJournal existing = journalMapper.selectById(j.getId());
        if (existing != null && !existing.getUserId().equals(currentUserId())) {
            return R.fail(403, "只能修改自己的日志");
        }
        journalMapper.updateById(j);
        return R.ok(j);
    }

    @DeleteMapping("/journals/{id}")
    @Operation(summary = "删除日志")
    public R<Void> deleteJournal(@PathVariable Long id) {
        ProjectJournal existing = journalMapper.selectById(id);
        if (existing != null && !existing.getUserId().equals(currentUserId())) {
            return R.fail(403, "只能删除自己的日志");
        }
        journalMapper.deleteById(id);
        return R.ok();
    }

    // ========== Git提交记录 ==========

    @GetMapping("/git-commits")
    @Operation(summary = "查询Git提交记录（学生仅看自己小组）")
    public R<Map<String, Object>> listGitCommits(@RequestParam(required = false) Long groupId,
                                                 @RequestParam(required = false) Long userId,
                                                 PageQuery q) {
        var wq = new LambdaQueryWrapper<GitCommitLog>();
        // 学生数据隔离
        List<Long> studentGroupIds = getStudentGroupIds();
        if (!studentGroupIds.isEmpty()) {
            wq.in(GitCommitLog::getGroupId, studentGroupIds);
        } else if (groupId != null) {
            wq.eq(GitCommitLog::getGroupId, groupId);
        }
        if (userId != null) wq.eq(GitCommitLog::getUserId, userId);
        wq.orderByDesc(GitCommitLog::getCommittedAt);
        Page<GitCommitLog> page = gitCommitMapper.selectPage(new Page<>(q.getPage(), q.getSize()), wq);
        List<Map<String, Object>> enriched = page.getRecords().stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId()); m.put("userId", c.getUserId()); m.put("groupId", c.getGroupId());
            m.put("repoName", c.getRepoName()); m.put("commitHash", c.getCommitHash());
            m.put("message", c.getMessage()); m.put("additions", c.getAdditions());
            m.put("deletions", c.getDeletions()); m.put("committedAt", c.getCommittedAt());
            m.put("syncedAt", c.getSyncedAt());
            var u = userMapper.selectById(c.getUserId());
            m.put("userName", u != null ? u.getRealName() : "");
            return m;
        }).toList();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", enriched); result.put("total", page.getTotal());
        result.put("size", page.getSize()); result.put("current", page.getCurrent());
        result.put("pages", page.getPages());
        return R.ok(result);
    }

    @PostMapping("/git-commits")
    @Operation(summary = "手动添加Git提交记录")
    public R<GitCommitLog> createGitCommit(@RequestBody GitCommitLog c) {
        c.setUserId(currentUserId());
        gitCommitMapper.insert(c);
        return R.ok(c);
    }

    @PutMapping("/git-commits/{id}")
    @Operation(summary = "更新Git提交记录")
    public R<GitCommitLog> updateGitCommit(@PathVariable Long id, @RequestBody GitCommitLog c) {
        c.setId(id);
        gitCommitMapper.updateById(c);
        return R.ok(c);
    }

    @DeleteMapping("/git-commits/{id}")
    @Operation(summary = "删除Git提交记录")
    public R<Void> deleteGitCommit(@PathVariable Long id) {
        GitCommitLog existing = gitCommitMapper.selectById(id);
        if (existing != null && !existing.getUserId().equals(currentUserId()) && !isTeacherOrAdmin()) {
            return R.fail(403, "只能删除自己的提交记录");
        }
        gitCommitMapper.deleteById(id);
        return R.ok();
    }

    // ========== 任务状态变更 ==========

    @PutMapping("/tasks/{id}/status")
    @Operation(summary = "变更任务状态")
    public R<ProjectTask> moveTask(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        ProjectTask t = taskMapper.selectById(id);
        if (t == null) return R.fail(404, "任务不存在");
        String status = (String) body.get("status");
        if (status != null) t.setStatus(status);
        taskMapper.updateById(t);
        return R.ok(t);
    }

    // ========== 贡献记录 ==========

    @GetMapping("/contributions")
    @Operation(summary = "查询贡献记录（学生仅看自己小组，已解析姓名）")
    public R<Map<String, Object>> listContributions(@RequestParam(required = false) Long groupId,
                                                       @RequestParam(required = false) Long userId,
                                                       PageQuery q) {
        var wq = new LambdaQueryWrapper<ContributionLog>();
        List<Long> studentGroupIds = getStudentGroupIds();
        if (!studentGroupIds.isEmpty()) {
            wq.in(ContributionLog::getGroupId, studentGroupIds);
        } else if (groupId != null) {
            wq.eq(ContributionLog::getGroupId, groupId);
        }
        if (userId != null) wq.eq(ContributionLog::getUserId, userId);
        wq.orderByDesc(ContributionLog::getCreatedAt);
        Page<ContributionLog> page = contributionMapper.selectPage(new Page<>(q.getPage(), q.getSize()), wq);
        List<Map<String, Object>> enriched = page.getRecords().stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId()); m.put("groupId", c.getGroupId()); m.put("userId", c.getUserId());
            m.put("description", c.getDescription()); m.put("bonus", c.getBonusScore());
            m.put("approved", c.getApproved()); m.put("approvedBy", c.getApprovedBy());
            m.put("createdAt", c.getCreatedAt());
            var u = userMapper.selectById(c.getUserId());
            m.put("userName", u != null ? u.getRealName() : "");
            m.put("realName", u != null ? u.getRealName() : "");
            if (c.getApprovedBy() != null) {
                var approver = userMapper.selectById(c.getApprovedBy());
                m.put("approverName", approver != null ? approver.getRealName() : "");
            }
            return m;
        }).toList();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", enriched); result.put("total", page.getTotal());
        result.put("size", page.getSize()); result.put("current", page.getCurrent()); result.put("pages", page.getPages());
        return R.ok(result);
    }

    @PostMapping("/contributions")
    @Operation(summary = "提交贡献记录")
    public R<ContributionLog> createContribution(@RequestBody ContributionLog c) {
        c.setUserId(currentUserId());
        c.setApproved(false);
        contributionMapper.insert(c);
        return R.ok(c);
    }

    @PutMapping("/contributions/{id}")
    @Operation(summary = "编辑贡献记录")
    public R<ContributionLog> updateContribution(@PathVariable Long id, @RequestBody ContributionLog c) {
        c.setId(id);
        ContributionLog existing = contributionMapper.selectById(id);
        if (existing != null && !existing.getUserId().equals(currentUserId())) {
            return R.fail(403, "只能编辑自己的贡献记录");
        }
        contributionMapper.updateById(c);
        return R.ok(c);
    }

    @PutMapping("/contributions/{id}/approve")
    @Operation(summary = "审批贡献记录")
    public R<ContributionLog> approveContribution(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "true") boolean approved) {
        if (!isTeacherOrAdmin()) return R.fail(403, "只有教师或管理员可以审批");
        ContributionLog existing = contributionMapper.selectById(id);
        if (existing == null) return R.fail(404, "贡献记录不存在");
        Long approverId = currentUserId();
        if (existing.getUserId().equals(approverId)) {
            return R.fail(400, "不能审批自己的贡献记录");
        }
        existing.setApproved(approved);
        existing.setApprovedBy(approverId);
        contributionMapper.updateById(existing);
        return R.ok(existing);
    }

    @DeleteMapping("/contributions/{id}")
    @Operation(summary = "删除贡献记录")
    public R<Void> deleteContribution(@PathVariable Long id) {
        ContributionLog existing = contributionMapper.selectById(id);
        if (existing != null && !existing.getUserId().equals(currentUserId())) {
            return R.fail(403, "只能删除自己的贡献记录");
        }
        contributionMapper.deleteById(id);
        return R.ok();
    }

    // ========== Git仓库配置 ==========

    @GetMapping("/git-repos")
    @Operation(summary = "查询Git仓库配置列表")
    public R<List<RepoConfig>> listGitRepos(@RequestParam(required = false) Long groupId) {
        var wq = new LambdaQueryWrapper<RepoConfig>();
        if (groupId != null) wq.eq(RepoConfig::getGroupId, groupId);
        List<RepoConfig> repos = repoConfigMapper.selectList(wq);
        repos.forEach(r -> r.setAccessToken(null));
        return R.ok(repos);
    }

    @PostMapping("/git-repos")
    @Operation(summary = "创建Git仓库配置")
    public R<RepoConfig> createGitRepo(@RequestBody RepoConfig config) {
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

    @PutMapping("/git-repos/{id}")
    @Operation(summary = "更新Git仓库配置")
    public R<RepoConfig> updateGitRepo(@PathVariable Long id, @RequestBody RepoConfig config) {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        config.setId(id);
        RepoConfig existing = repoConfigMapper.selectById(id);
        if (existing == null) return R.fail(404, "仓库配置不存在");
        if (config.getAccessToken() == null || config.getAccessToken().isEmpty()) {
            config.setAccessToken(existing.getAccessToken());
        }
        repoConfigMapper.updateById(config);
        config.setAccessToken(null);
        return R.ok(config);
    }

    @DeleteMapping("/git-repos/{id}")
    @Operation(summary = "删除Git仓库配置")
    public R<Void> deleteGitRepo(@PathVariable Long id) {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        repoConfigMapper.deleteById(id);
        return R.ok();
    }

    @PostMapping("/git-repos/{id}/sync")
    @Operation(summary = "同步单个Git仓库")
    public R<Map<String, Object>> syncGitRepo(@PathVariable Long id) {
        if (!isTeacherOrAdmin()) return R.fail(403, "无权限");
        RepoConfig repo = repoConfigMapper.selectById(id);
        if (repo == null) return R.fail(404, "仓库配置不存在");
        Map<String, Object> result = gitSyncService.syncByGroupId(repo.getGroupId());
        return R.ok(result);
    }

    // ========== 需求变更记录 ==========

    @GetMapping("/requirement-changes")
    @Operation(summary = "查询需求变更记录")
    public R<List<Map<String, Object>>> listReqChanges(@RequestParam(required = false) Long groupId) {
        var wq = new LambdaQueryWrapper<RequirementChange>().orderByDesc(RequirementChange::getCreatedAt);
        if (groupId != null) wq.eq(RequirementChange::getGroupId, groupId);
        List<RequirementChange> changes = reqChangeMapper.selectList(wq);
        List<Map<String, Object>> result = new ArrayList<>();
        for (var c : changes) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId()); m.put("groupId", c.getGroupId()); m.put("title", c.getTitle());
            m.put("description", c.getDescription()); m.put("changeType", c.getChangeType());
            m.put("reason", c.getReason()); m.put("createdAt", c.getCreatedAt());
            var user = userMapper.selectById(c.getCreatedBy());
            m.put("createdByName", user != null ? user.getRealName() : "");
            result.add(m);
        }
        return R.ok(result);
    }

    @PostMapping("/requirement-changes")
    @Operation(summary = "记录需求变更")
    public R<RequirementChange> addReqChange(@RequestBody RequirementChange c) {
        c.setCreatedBy(currentUserId());
        c.setCreatedAt(LocalDateTime.now());
        reqChangeMapper.insert(c);
        return R.ok(c);
    }
}
