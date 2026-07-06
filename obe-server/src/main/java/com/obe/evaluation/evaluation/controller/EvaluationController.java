package com.obe.evaluation.evaluation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.obe.evaluation.common.PageQuery;
import com.obe.evaluation.common.R;
import com.obe.evaluation.evaluation.entity.*;
import com.obe.evaluation.evaluation.mapper.*;
import com.obe.evaluation.group.entity.GroupMember;
import com.obe.evaluation.group.mapper.GroupMemberMapper;
import com.obe.evaluation.evaluation.service.EvaluationService;
import com.obe.evaluation.evaluation.entity.ScoreDispute;
import com.obe.evaluation.evaluation.mapper.ScoreDisputeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.obe.evaluation.system.mapper.SysUserMapper;
import com.obe.evaluation.group.entity.ProjectGroup;
import com.obe.evaluation.group.mapper.ProjectGroupMapper;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
@Tag(name = "评价模块")
public class EvaluationController {

    private final GroupEvaluationMapper groupEvalMapper;
    private final RoleEvaluationMapper roleEvalMapper;
    private final PersonalScoreMapper scoreMapper;
    private final EvaluationService evaluationService;
    private final ScoreDisputeMapper disputeMapper;
    private final SysUserMapper userMapper;
    private final ProjectGroupMapper groupMapper;
    private final PositionCriteriaMapper positionCriteriaMapper;
    private final GroupMemberMapper memberMapper;

    // ========== 岗位专项评价 ==========

    @GetMapping("/position-criteria")
    @Operation(summary = "查询岗位评价维度")
    public R<List<PositionCriteria>> positionCriteria(@RequestParam(required = false) String roleCode) {
        if (roleCode != null) return R.ok(positionCriteriaMapper.selectList(
            new LambdaQueryWrapper<PositionCriteria>().eq(PositionCriteria::getRoleCode, roleCode)));
        return R.ok(positionCriteriaMapper.selectList(null));
    }

    @GetMapping("/position-criteria/{groupId}")
    @Operation(summary = "查询小组成员及其岗位评价维度")
    public R<List<Map<String, Object>>> memberPositionCriteria(@PathVariable Long groupId) {
        List<GroupMember> members = memberMapper.selectList(
            new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (GroupMember m : members) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("memberId", m.getId()); item.put("userId", m.getUserId());
            var user = userMapper.selectById(m.getUserId());
            item.put("userName", user != null ? user.getRealName() : "");
            item.put("roleCode", m.getRoleCode());
            var criteria = positionCriteriaMapper.selectList(
                new LambdaQueryWrapper<PositionCriteria>().eq(PositionCriteria::getRoleCode, m.getRoleCode()));
            item.put("criteria", criteria);
            result.add(item);
        }
        return R.ok(result);
    }

    private String userName(Long id) {
        if (id == null) return "";
        var u = userMapper.selectById(id);
        return u != null ? u.getRealName() : String.valueOf(id);
    }
    private String groupName(Long id) {
        if (id == null) return "";
        ProjectGroup g = groupMapper.findByIdIncludeDeleted(id);
        if (g != null) {
            return g.getGroupName() + (g.getDeleted() != null && g.getDeleted() == 1 ? "(已删除)" : "");
        }
        return "(小组" + id + ")";
    }
    private Map<String, Object> enrichGroupEval(GroupEvaluation e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", e.getId()); m.put("groupId", e.getGroupId()); m.put("evaluatorId", e.getEvaluatorId());
        m.put("dimension", e.getDimension()); m.put("score", e.getScore()); m.put("comment", e.getComment());
        m.put("createdAt", e.getCreatedAt());
        m.put("evaluatorName", userName(e.getEvaluatorId()));
        m.put("groupName", groupName(e.getGroupId()));
        return m;
    }
    private Map<String, Object> enrichRoleEval(RoleEvaluation e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", e.getId()); m.put("groupId", e.getGroupId()); m.put("userId", e.getUserId());
        m.put("evaluatorId", e.getEvaluatorId()); m.put("roleCode", e.getRoleCode());
        m.put("score", e.getScore()); m.put("comment", e.getComment()); m.put("createdAt", e.getCreatedAt());
        m.put("evaluatorName", userName(e.getEvaluatorId()));
        m.put("userName", userName(e.getUserId()));
        m.put("groupName", groupName(e.getGroupId()));
        return m;
    }
    private Map<String, Object> enrichScore(PersonalScore s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getId()); m.put("userId", s.getUserId()); m.put("groupId", s.getGroupId());
        m.put("groupTotalScore", s.getGroupTotalScore()); m.put("contributionRatio", s.getContributionRatio());
        m.put("finalScore", s.getFinalScore()); m.put("bonusTotal", s.getBonusTotal());
        m.put("calculatedAt", s.getCalculatedAt());
        m.put("userName", userName(s.getUserId()));
        m.put("groupName", groupName(s.getGroupId()));
        return m;
    }

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long))
            throw new IllegalArgumentException("未登录");
        return (Long) auth.getPrincipal();
    }

    private boolean isAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isTeacherOrAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TEACHER"));
    }

    // ========== 小组评价 ==========

    @GetMapping("/group")
    @Operation(summary = "查询小组评价列表（已解析姓名）")
    public R<List<Map<String, Object>>> listGroupEvals(@RequestParam(required = false) Long groupId,
                                                    @RequestParam(required = false) Long evaluatorId) {
        var wq = new LambdaQueryWrapper<GroupEvaluation>();
        if (groupId != null) wq.eq(GroupEvaluation::getGroupId, groupId);
        if (evaluatorId != null) wq.eq(GroupEvaluation::getEvaluatorId, evaluatorId);
        wq.orderByDesc(GroupEvaluation::getCreatedAt);
        return R.ok(groupEvalMapper.selectList(wq).stream().map(this::enrichGroupEval).toList());
    }

    @PostMapping("/group")
    @Operation(summary = "提交小组评价")
    public R<GroupEvaluation> createGroupEval(@RequestBody GroupEvaluation e) {
        if (e.getGroupId() == null) return R.fail(400, "请选择小组");
        if (e.getDimension() == null || e.getDimension().isBlank()) return R.fail(400, "评价维度不能为空");
        if (e.getScore() == null) return R.fail(400, "评分不能为空");
        e.setEvaluatorId(currentUserId());
        groupEvalMapper.insert(e);
        return R.ok(e);
    }

    @PutMapping("/group/{id}")
    @Operation(summary = "更新小组评价")
    public R<GroupEvaluation> updateGroupEval(@PathVariable Long id, @RequestBody GroupEvaluation e) {
        e.setId(id);
        GroupEvaluation existing = groupEvalMapper.selectById(e.getId());
        if (existing != null && !existing.getEvaluatorId().equals(currentUserId()) && !isAdmin()) {
            return R.fail(403, "只能修改自己的评价");
        }
        groupEvalMapper.updateById(e);
        return R.ok(e);
    }

    @DeleteMapping("/group/{id}")
    @Operation(summary = "删除小组评价")
    public R<Void> deleteGroupEval(@PathVariable Long id) {
        GroupEvaluation existing = groupEvalMapper.selectById(id);
        if (existing != null && !existing.getEvaluatorId().equals(currentUserId()) && !isAdmin()) {
            return R.fail(403, "只能删除自己的评价");
        }
        groupEvalMapper.deleteById(id);
        return R.ok();
    }

    // ========== 角色评价 ==========

    @GetMapping("/role")
    @Operation(summary = "查询角色评价列表（已解析姓名）")
    public R<List<Map<String, Object>>> listRoleEvals(@RequestParam(required = false) Long groupId,
                                                  @RequestParam(required = false) Long userId,
                                                  @RequestParam(required = false) Long evaluatorId) {
        var wq = new LambdaQueryWrapper<RoleEvaluation>();
        if (groupId != null) wq.eq(RoleEvaluation::getGroupId, groupId);
        if (userId != null) wq.eq(RoleEvaluation::getUserId, userId);
        if (evaluatorId != null) wq.eq(RoleEvaluation::getEvaluatorId, evaluatorId);
        wq.orderByDesc(RoleEvaluation::getCreatedAt);
        return R.ok(roleEvalMapper.selectList(wq).stream().map(this::enrichRoleEval).toList());
    }

    @PostMapping("/role")
    @Operation(summary = "提交角色评价")
    public R<RoleEvaluation> createRoleEval(@RequestBody RoleEvaluation e) {
        if (e.getUserId() == null) return R.fail(400, "请选择被评价的学生");
        if (e.getGroupId() == null) return R.fail(400, "请选择小组");
        if (e.getDimension() == null || e.getDimension().isBlank()) return R.fail(400, "评价维度不能为空");
        e.setEvaluatorId(currentUserId());
        roleEvalMapper.insert(e);
        return R.ok(e);
    }

    @PutMapping("/role/{id}")
    @Operation(summary = "更新角色评价")
    public R<RoleEvaluation> updateRoleEval(@PathVariable Long id, @RequestBody RoleEvaluation e) {
        e.setId(id);
        RoleEvaluation existing = roleEvalMapper.selectById(e.getId());
        if (existing != null && !existing.getEvaluatorId().equals(currentUserId()) && !isAdmin()) {
            return R.fail(403, "只能修改自己的评价");
        }
        roleEvalMapper.updateById(e);
        return R.ok(e);
    }

    @DeleteMapping("/role/{id}")
    @Operation(summary = "删除角色评价")
    public R<Void> deleteRoleEval(@PathVariable Long id) {
        RoleEvaluation existing = roleEvalMapper.selectById(id);
        if (existing != null && !existing.getEvaluatorId().equals(currentUserId()) && !isAdmin()) {
            return R.fail(403, "只能删除自己的评价");
        }
        roleEvalMapper.deleteById(id);
        return R.ok();
    }

    // ========== 个人成绩查询 ==========

    @GetMapping("/personal-scores")
    @Operation(summary = "查询个人成绩（学生仅看自己，教师看自己小组，管理员看全部）")
    public R<List<Map<String, Object>>> listPersonalScores(@RequestParam(required = false) Long groupId) {
        var wq = new LambdaQueryWrapper<PersonalScore>()
                .orderByDesc(PersonalScore::getCalculatedAt);
        if (!isTeacherOrAdmin()) {
            // 学生强制只看自己的成绩
            wq.eq(PersonalScore::getUserId, currentUserId());
        } else if (!isAdmin()) {
            // 教师只看自己管理的小组成绩
            List<Long> teacherGroupIds = groupMapper.selectList(
                new LambdaQueryWrapper<ProjectGroup>().eq(ProjectGroup::getTeacherId, currentUserId()))
                .stream().map(ProjectGroup::getId).toList();
            if (!teacherGroupIds.isEmpty()) wq.in(PersonalScore::getGroupId, teacherGroupIds);
            else return R.ok(List.of()); // 无管理小组时返回空
        }
        if (groupId != null) wq.eq(PersonalScore::getGroupId, groupId);
        return R.ok(scoreMapper.selectList(wq).stream().map(this::enrichScore).toList());
    }

    @PostMapping("/calculate")
    @Operation(summary = "批量计算小组所有成员成绩")
    public R<Map<String, Object>> calculateAll(@RequestParam Long groupId) {
        return R.ok(evaluationService.calculateBatch(groupId));
    }

    // ========== 个人成绩 ==========

    @GetMapping("/scores")
    @Operation(summary = "查询个人成绩列表")
    public R<Page<PersonalScore>> listScores(@RequestParam(required = false) Long groupId,
                                              @RequestParam(required = false) Long userId,
                                              PageQuery q) {
        var wq = new LambdaQueryWrapper<PersonalScore>();
        if (groupId != null) wq.eq(PersonalScore::getGroupId, groupId);
        if (userId != null) wq.eq(PersonalScore::getUserId, userId);
        wq.orderByDesc(PersonalScore::getCalculatedAt);
        return R.ok(scoreMapper.selectPage(new Page<>(q.getPage(), q.getSize()), wq));
    }

    @PostMapping("/scores/calculate/{userId}")
    @Operation(summary = "计算单个学生的个人成绩")
    public R<PersonalScore> calculateSingle(@PathVariable Long userId,
                                             @RequestParam Long groupId) {
        PersonalScore score = evaluationService.calculatePersonalScore(userId, groupId);
        return R.ok(score);
    }

    @PostMapping("/scores/calculate-batch/{groupId}")
    @Operation(summary = "批量计算小组所有成员的个人成绩")
    public R<Map<String, Object>> calculateBatch(@PathVariable Long groupId) {
        Map<String, Object> result = evaluationService.calculateBatch(groupId);
        return R.ok(result);
    }

    @GetMapping("/scores/my")
    @Operation(summary = "查询我的个人成绩（已解析姓名）")
    public R<List<Map<String, Object>>> myScores() {
        Long userId = currentUserId();
        List<PersonalScore> scores = scoreMapper.selectList(
                new LambdaQueryWrapper<PersonalScore>()
                        .eq(PersonalScore::getUserId, userId)
                        .orderByDesc(PersonalScore::getCalculatedAt));
        return R.ok(scores.stream().map(this::enrichScore).toList());
    }

    // ========== 成绩争议 ==========

    @PostMapping("/dispute")
    @Operation(summary = "提交成绩争议")
    public R<ScoreDispute> createDispute(@RequestBody Map<String, Object> body) {
        Long scoreId = body.get("scoreId") != null ? Long.valueOf(body.get("scoreId").toString()) : null;
        String reason = (String) body.getOrDefault("reason", "");
        String type = (String) body.getOrDefault("type", "GROUP");
        if (scoreId == null) return R.fail(400, "scoreId不能为空");
        if (!List.of("GROUP", "ROLE").contains(type)) return R.fail(400, "type必须为GROUP或ROLE");
        ScoreDispute dispute = new ScoreDispute();
        dispute.setUserId(currentUserId());
        dispute.setScoreId(scoreId);
        dispute.setScoreType(type);
        dispute.setReason(reason);
        dispute.setStatus("PENDING");
        dispute.setCreatedAt(LocalDateTime.now());
        dispute.setUpdatedAt(LocalDateTime.now());
        disputeMapper.insert(dispute);
        return R.ok(dispute);
    }

    @GetMapping("/dispute")
    @Operation(summary = "查询争议列表")
    public R<List<ScoreDispute>> listDisputes(@RequestParam(required = false) Long userId) {
        var wq = new LambdaQueryWrapper<ScoreDispute>().orderByDesc(ScoreDispute::getCreatedAt);
        if (userId != null) wq.eq(ScoreDispute::getUserId, userId);
        return R.ok(disputeMapper.selectList(wq));
    }

    // ========== 汇总 ==========

    @GetMapping("/summary/{groupId}")
    @Operation(summary = "获取小组评价汇总（已解析姓名）")
    public R<Map<String, Object>> summary(@PathVariable Long groupId) {
        List<GroupEvaluation> groupEvals = groupEvalMapper.selectList(
                new LambdaQueryWrapper<GroupEvaluation>().eq(GroupEvaluation::getGroupId, groupId));
        List<RoleEvaluation> roleEvals = roleEvalMapper.selectList(
                new LambdaQueryWrapper<RoleEvaluation>().eq(RoleEvaluation::getGroupId, groupId));
        List<PersonalScore> scores = scoreMapper.selectList(
                new LambdaQueryWrapper<PersonalScore>().eq(PersonalScore::getGroupId, groupId));
        return R.ok(Map.of(
                "groupEvaluations", groupEvals.stream().map(this::enrichGroupEval).toList(),
                "roleEvaluations", roleEvals.stream().map(this::enrichRoleEval).toList(),
                "personalScores", scores.stream().map(this::enrichScore).toList()
        ));
    }
}
