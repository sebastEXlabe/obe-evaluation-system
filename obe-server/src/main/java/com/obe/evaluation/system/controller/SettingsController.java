package com.obe.evaluation.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.common.R;
import com.obe.evaluation.analysis.entity.AchievementResult;
import com.obe.evaluation.analysis.mapper.AchievementResultMapper;
import com.obe.evaluation.evaluation.entity.PersonalScore;
import com.obe.evaluation.evaluation.mapper.PersonalScoreMapper;
import com.obe.evaluation.group.entity.GroupMember;
import com.obe.evaluation.group.entity.ProjectGroup;
import com.obe.evaluation.group.mapper.GroupMemberMapper;
import com.obe.evaluation.group.mapper.ProjectGroupMapper;
import com.obe.evaluation.system.entity.Course;
import com.obe.evaluation.system.entity.SysUser;
import com.obe.evaluation.system.mapper.CourseMapper;
import com.obe.evaluation.system.mapper.SysUserMapper;
import com.obe.evaluation.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Tag(name = "设置接口")
public class SettingsController {

    private final SysUserService userService;
    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProjectGroupMapper groupMapper;
    private final GroupMemberMapper memberMapper;
    private final PersonalScoreMapper scoreMapper;
    private final CourseMapper courseMapper;
    private final AchievementResultMapper achievementResultMapper;

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long id))
            throw new IllegalArgumentException("未登录");
        return id;
    }

    // ========== 个人资料 ==========

    @GetMapping("/profile")
    @Operation(summary = "获取当前用户资料")
    public R<SysUser> profile() {
        SysUser user = userService.getById(currentUserId());
        if (user != null) user.setPassword(null);
        return R.ok(user);
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人信息")
    public R<Void> updateProfile(@RequestBody Map<String, String> body) {
        SysUser u = new SysUser();
        u.setId(currentUserId());
        if (body.containsKey("username")) u.setUsername(body.get("username"));
        if (body.containsKey("realName")) u.setRealName(body.get("realName"));
        if (body.containsKey("email")) u.setEmail(body.get("email"));
        if (body.containsKey("phone")) u.setPhone(body.get("phone"));
        userService.updateById(u);
        return R.ok();
    }

    // ========== 密码 ==========

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public R<Void> changePassword(@RequestBody Map<String, String> body) {
        Long userId = currentUserId();
        String oldPwd = body.getOrDefault("oldPassword", "");
        String newPwd = body.getOrDefault("newPassword", "");
        if (StrUtil.isBlank(oldPwd) || StrUtil.isBlank(newPwd))
            return R.fail(400, "密码不能为空");
        if (newPwd.length() < 6) return R.fail(400, "新密码至少6位");
        SysUser user = userService.getById(userId);
        if (user == null || !passwordEncoder.matches(oldPwd, user.getPassword()))
            return R.fail(400, "旧密码错误");
        user.setPassword(passwordEncoder.encode(newPwd));
        userService.updateById(user);
        return R.ok();
    }

    // ========== Git身份 ==========

    @GetMapping("/git-identity")
    @Operation(summary = "获取Git身份绑定")
    public R<Map<String, String>> gitIdentity() {
        SysUser user = userService.getById(currentUserId());
        Map<String, String> result = new LinkedHashMap<>();
        result.put("gitUsername", user != null && user.getGitUsername() != null ? user.getGitUsername() : "");
        result.put("gitEmail", user != null && user.getGitEmail() != null ? user.getGitEmail() : "");
        return R.ok(result);
    }

    @PutMapping("/git-identity")
    @Operation(summary = "绑定Git身份")
    public R<Void> bindGit(@RequestBody Map<String, String> body) {
        SysUser u = new SysUser();
        u.setId(currentUserId());
        u.setGitUsername(body.get("gitUsername"));
        u.setGitEmail(body.get("gitEmail"));
        userService.updateById(u);
        return R.ok();
    }

    private boolean isTeacherOrAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TEACHER"));
    }

    // ========== 小组概览（教师/管理员） ==========

    @GetMapping("/group-overview")
    @Operation(summary = "获取小组概览")
    public R<List<Map<String, Object>>> groupOverview() {
        if (!isTeacherOrAdmin()) return R.fail(403, "仅教师和管理员可查看小组概览");
        List<ProjectGroup> groups = groupMapper.selectList(null);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ProjectGroup g : groups) {
            Long memberCount = memberMapper.selectCount(
                    new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, g.getId()));
            // Look up course name
            String courseName = "";
            if (g.getCourseId() != null) {
                Course course = courseMapper.selectById(g.getCourseId());
                courseName = course != null ? course.getCourseName() : "";
            }
            // Compute overall achievement (average of achievementValue for the group)
            Double overallAchievement = null;
            List<AchievementResult> achievements = achievementResultMapper.selectList(
                    new LambdaQueryWrapper<AchievementResult>().eq(AchievementResult::getGroupId, g.getId()));
            if (achievements != null && !achievements.isEmpty()) {
                double sum = 0;
                int count = 0;
                for (AchievementResult ar : achievements) {
                    if (ar.getAchievementValue() != null) {
                        sum += ar.getAchievementValue();
                        count++;
                    }
                }
                if (count > 0) overallAchievement = sum / count;
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", g.getId());
            item.put("name", g.getGroupName());
            item.put("courseName", courseName);
            item.put("memberCount", memberCount);
            item.put("maxMembers", g.getMaxMembers() != null ? g.getMaxMembers() : 8);
            item.put("overallAchievement", overallAchievement);
            result.add(item);
        }
        return R.ok(result);
    }

    // ========== 学生达成度查询 ==========

    @GetMapping("/student-achievements")
    @Operation(summary = "查询学生达成度")
    public R<List<Map<String, Object>>> studentAchievements(@RequestParam Long groupId) {
        // Ownership check: only admin/teacher can view; teacher can only see their own course groups
        if (!isTeacherOrAdmin()) {
            return R.fail(403, "仅教师和管理员可查看学生达成度");
        }
        ProjectGroup group = groupMapper.selectById(groupId);
        if (group == null) return R.fail(404, "小组不存在");
        // Non-admin teacher must own the course associated with this group
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && group.getCourseId() != null) {
            Course course = courseMapper.selectById(group.getCourseId());
            if (course == null || !course.getTeacherId().equals(currentUserId())) {
                return R.fail(403, "只能查看自己课程的学生达成度");
            }
        }
        List<GroupMember> members = memberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (GroupMember member : members) {
            SysUser user = userMapper.selectById(member.getUserId());
            PersonalScore score = scoreMapper.selectOne(
                    new LambdaQueryWrapper<PersonalScore>()
                            .eq(PersonalScore::getUserId, member.getUserId())
                            .eq(PersonalScore::getGroupId, groupId));
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("userId", member.getUserId());
            item.put("realName", user != null ? user.getRealName() : "");
            item.put("username", user != null ? user.getUsername() : "");
            item.put("groupScore", score != null ? score.getGroupTotalScore() : null);
            item.put("contributionRatio", score != null ? score.getContributionRatio() : null);
            item.put("bonus", score != null ? score.getBonusTotal() : null);
            item.put("finalScore", score != null ? score.getFinalScore() : null);
            item.put("achievement", score != null && score.getFinalScore() != null ? score.getFinalScore() / 100.0 : null);
            result.add(item);
        }
        return R.ok(result);
    }
}
