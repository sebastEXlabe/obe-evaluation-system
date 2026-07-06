/**
 * 小组管理控制器 — CDIO组织架构
 * 教师创建小组、学生邀请码加入、成员角色管理、岗位轮换记录
 * 数据隔离：学生只看自己组，教师只看自己管理的组
 */
package com.obe.evaluation.group.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.common.R;
import com.obe.evaluation.group.entity.GroupMember;
import com.obe.evaluation.group.entity.PositionHistory;
import com.obe.evaluation.group.entity.ProjectGroup;
import com.obe.evaluation.group.mapper.PositionHistoryMapper;
import com.obe.evaluation.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.obe.evaluation.system.entity.SysUser;
import com.obe.evaluation.system.mapper.SysUserMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "模块二：小组分工与角色", description = "CDIO组织架构")
public class GroupController {

    private final GroupService groupService;
    private final SysUserMapper userMapper;
    private final PositionHistoryMapper historyMapper;

    private Long currentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean isTeacherOrAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TEACHER"));
    }

    private boolean isAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isGroupTeacher(Long groupId) {
        ProjectGroup group = groupService.getById(groupId);
        return group != null && group.getTeacherId().equals(currentUserId());
    }

    // ========== 小组 ==========

    @GetMapping
    @Operation(summary = "查询小组（按角色过滤：学生只看自己的组，教师看管理的组，管理员看全部）")
    public R<List<Map<String, Object>>> listGroups(@RequestParam(required = false) Long courseId) {
        List<ProjectGroup> groups;
        if (!isTeacherOrAdmin()) {
            // 学生：只显示自己所在的小组
            Long userId = currentUserId();
            var memberships = groupService.getMemberMapper().selectList(
                new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getUserId, userId));
            List<Long> groupIds = memberships.stream().map(GroupMember::getGroupId).toList();
            if (groupIds.isEmpty()) return R.ok(List.of());
            groups = groupService.listByIds(groupIds);
            // 过滤已删除
            groups = groups.stream().filter(g -> g.getDeleted() == null || g.getDeleted() == 0).toList();
        } else if (isAdmin()) {
            if (courseId != null) {
                groups = groupService.list(new LambdaQueryWrapper<ProjectGroup>().eq(ProjectGroup::getCourseId, courseId));
            } else {
                groups = groupService.list();
            }
        } else {
            // 教师：只看自己创建的小组
            Long userId = currentUserId();
            var wq = new LambdaQueryWrapper<ProjectGroup>().eq(ProjectGroup::getTeacherId, userId);
            if (courseId != null) wq.eq(ProjectGroup::getCourseId, courseId);
            groups = groupService.list(wq);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (ProjectGroup g : groups) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", g.getId()); item.put("groupName", g.getGroupName());
            item.put("teacherId", g.getTeacherId()); item.put("courseId", g.getCourseId());
            item.put("inviteCode", g.getInviteCode()); item.put("maxMembers", g.getMaxMembers());
            item.put("status", g.getStatus()); item.put("createdAt", g.getCreatedAt());
            item.put("updatedAt", g.getUpdatedAt());
            SysUser teacher = userMapper.selectById(g.getTeacherId());
            item.put("teacherName", teacher != null ? teacher.getRealName() : "");
            result.add(item);
        }
        return R.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询小组详情（含成员）")
    public R<Map<String, Object>> getGroup(@PathVariable Long id) {
        Map<String, Object> detail = groupService.getGroupDetail(id);
        if (detail.isEmpty()) return R.fail(404, "小组不存在");
        return R.ok(detail);
    }

    @PostMapping
    @Operation(summary = "创建小组")
    public R<ProjectGroup> createGroup(@RequestBody ProjectGroup group) {
        groupService.save(group);
        return R.ok(group);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新小组")
    public R<ProjectGroup> updateGroup(@PathVariable Long id, @RequestBody ProjectGroup group) {
        if (!isTeacherOrAdmin() && !isGroupTeacher(id)) return R.fail(403, "只有小组教师或管理员可以编辑");
        group.setId(id);
        groupService.updateById(group);
        return R.ok(group);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除小组")
    public R<Void> deleteGroup(@PathVariable Long id) {
        groupService.removeGroupCascade(id);
        return R.ok();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "设置小组状态 (0=归档, 1=活跃)")
    public R<ProjectGroup> setGroupStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        if (!isTeacherOrAdmin()) return R.fail(403, "只有教师或管理员可以修改状态");
        ProjectGroup group = groupService.getById(id);
        if (group == null) return R.fail(404, "小组不存在");
        Integer status = (Integer) body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return R.fail(400, "status必须为0(归档)或1(活跃)");
        }
        group.setStatus(status);
        groupService.updateById(group);
        return R.ok(group);
    }

    // ========== 成员 ==========

    @GetMapping("/{groupId}/members")
    @Operation(summary = "查询小组成员列表（含用户信息）")
    public R<List<Map<String, Object>>> listMembers(@PathVariable Long groupId) {
        List<GroupMember> members = groupService.getMemberMapper().selectList(
                new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (GroupMember m : members) {
            SysUser user = userMapper.selectById(m.getUserId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", m.getId());
            item.put("userId", m.getUserId());
            item.put("realName", user != null ? user.getRealName() : "");
            item.put("username", user != null ? user.getUsername() : "");
            item.put("role", m.getRoleCode());
            item.put("roleCode", m.getRoleCode());
            item.put("joinTime", m.getJoinAt());
            item.put("createTime", m.getJoinAt());
            result.add(item);
        }
        return R.ok(result);
    }

    @PostMapping("/{groupId}/members")
    @Operation(summary = "学生加入小组")
    public R<GroupMember> joinGroup(@PathVariable Long groupId,
                                     @RequestBody Map<String, Object> body) {
        ProjectGroup group = groupService.getById(groupId);
        if (group == null) return R.fail(404, "小组不存在");
        if (group.getStatus() != null && group.getStatus() == 0)
            return R.fail(400, "小组已归档，无法加入");
        Long userId = Long.valueOf(body.get("userId").toString());
        String roleCode = (String) body.getOrDefault("roleCode", "DEVELOPER");
        return R.ok(groupService.joinGroup(groupId, userId, roleCode));
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    @Operation(summary = "移除小组成员")
    public R<Void> removeMember(@PathVariable Long groupId, @PathVariable Long memberId) {
        if (!isTeacherOrAdmin() && !isGroupTeacher(groupId)) return R.fail(403, "只有小组教师或管理员可以移除成员");
        var member = groupService.getMemberMapper().selectById(memberId);
        if (member == null || !member.getGroupId().equals(groupId))
            throw new IllegalArgumentException("成员不属于该小组");
        groupService.removeMember(memberId);
        return R.ok();
    }

    @PutMapping("/{groupId}/members/{memberId}")
    @Operation(summary = "更新小组成员角色（自动记录轮换历史）")
    public R<GroupMember> updateMemberRole(@PathVariable Long groupId, @PathVariable Long memberId,
                                           @RequestBody Map<String, String> body) {
        if (!isTeacherOrAdmin() && !isGroupTeacher(groupId)) return R.fail(403, "只有小组教师或管理员可以更新成员角色");
        String roleCode = body.get("roleCode");
        if (roleCode == null || !List.of("PM", "DEVELOPER", "TESTER", "DOC_ADMIN").contains(roleCode))
            return R.fail(400, "无效角色: " + roleCode);
        var member = groupService.getMemberMapper().selectById(memberId);
        if (member == null || !member.getGroupId().equals(groupId))
            return R.fail(404, "成员不属于该小组");

        // 记录岗位轮换历史
        String oldRole = member.getRoleCode();
        if (!roleCode.equals(oldRole)) {
            PositionHistory history = new PositionHistory();
            history.setGroupId(groupId);
            history.setUserId(member.getUserId());
            history.setOldRole(oldRole);
            history.setNewRole(roleCode);
            history.setChangedBy(currentUserId());
            history.setReason(body.getOrDefault("reason", ""));
            history.setCreatedAt(LocalDateTime.now());
            historyMapper.insert(history);
        }

        member.setRoleCode(roleCode);
        groupService.getMemberMapper().updateById(member);
        return R.ok(member);
    }

    @GetMapping("/{groupId}/position-history")
    @Operation(summary = "查询小组成员岗位轮换历史")
    public R<List<Map<String, Object>>> positionHistory(@PathVariable Long groupId) {
        List<PositionHistory> histories = historyMapper.selectList(
            new LambdaQueryWrapper<PositionHistory>().eq(PositionHistory::getGroupId, groupId)
                .orderByDesc(PositionHistory::getCreatedAt));
        List<Map<String, Object>> result = new ArrayList<>();
        for (PositionHistory h : histories) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", h.getId()); item.put("groupId", h.getGroupId()); item.put("userId", h.getUserId());
            item.put("oldRole", h.getOldRole()); item.put("newRole", h.getNewRole());
            item.put("reason", h.getReason()); item.put("createdAt", h.getCreatedAt());
            var user = userMapper.selectById(h.getUserId());
            item.put("userName", user != null ? user.getRealName() : "");
            var changer = userMapper.selectById(h.getChangedBy());
            item.put("changedByName", changer != null ? changer.getRealName() : "");
            result.add(item);
        }
        return R.ok(result);
    }

    // ========== 邀请码 ==========

    @PostMapping("/join-by-code")
    @Operation(summary = "通过邀请码加入小组（学生用）")
    public R<GroupMember> joinByCode(@RequestBody Map<String, Object> body) {
        String code = (String) body.get("inviteCode");
        if (code == null || code.isBlank()) return R.fail(400, "邀请码不能为空");
        String roleCode = (String) body.getOrDefault("roleCode", "DEVELOPER");
        if (!List.of("PM", "DEVELOPER", "TESTER", "DOC_ADMIN").contains(roleCode))
            return R.fail(400, "无效角色: " + roleCode);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        var group = groupService.getOne(new LambdaQueryWrapper<ProjectGroup>()
                .eq(ProjectGroup::getInviteCode, code));
        if (group == null) return R.fail(404, "邀请码无效");
        if (group.getStatus() != null && group.getStatus() == 0)
            return R.fail(400, "小组已归档，无法加入");
        return R.ok(groupService.joinGroup(group.getId(), userId, roleCode));
    }

    @GetMapping("/my-groups")
    @Operation(summary = "查询我所在的小组（学生用）")
    public R<List<Map<String, Object>>> myGroups() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        var members = groupService.getMemberMapper().selectList(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getUserId, userId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (GroupMember m : members) {
            var g = groupService.getGroupDetail(m.getGroupId());
            if (g.isEmpty()) continue; // 跳过已删除的小组
            g.put("myRole", m.getRoleCode());
            result.add(g);
        }
        return R.ok(result);
    }
}
