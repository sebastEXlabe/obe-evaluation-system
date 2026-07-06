package com.obe.evaluation.group.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.obe.evaluation.group.entity.*;
import com.obe.evaluation.group.mapper.*;
import com.obe.evaluation.system.mapper.CourseMapper;
import com.obe.evaluation.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service @RequiredArgsConstructor
public class GroupService extends ServiceImpl<ProjectGroupMapper, ProjectGroup> {
    private final GroupMemberMapper memberMapper;
    private final SysUserMapper userMapper;
    private final CourseMapper courseMapper;
    // Cascade-delete mappers
    private final com.obe.evaluation.analysis.mapper.AchievementResultMapper achievementResultMapper;
    private final com.obe.evaluation.analysis.mapper.ImprovementSuggestionMapper improvementSuggestionMapper;
    private final com.obe.evaluation.analysis.mapper.ImprovementTaskMapper improvementTaskMapper;
    private final com.obe.evaluation.project.mapper.ProjectMilestoneMapper projectMilestoneMapper;
    private final com.obe.evaluation.project.mapper.ProjectTaskMapper projectTaskMapper;
    private final com.obe.evaluation.project.mapper.GitCommitLogMapper gitCommitLogMapper;
    private final com.obe.evaluation.project.mapper.ProjectJournalMapper projectJournalMapper;
    private final com.obe.evaluation.project.mapper.ContributionLogMapper contributionLogMapper;
    private final com.obe.evaluation.evaluation.mapper.GroupEvaluationMapper groupEvalMapper;
    private final com.obe.evaluation.evaluation.mapper.RoleEvaluationMapper roleEvalMapper;
    private final com.obe.evaluation.evaluation.mapper.PersonalScoreMapper personalScoreMapper;
    private final com.obe.evaluation.qa.mapper.QuestionAnswerMapper questionAnswerMapper;
    private final com.obe.evaluation.project.mapper.RepoConfigMapper repoConfigMapper;
    private final com.obe.evaluation.project.mapper.RequirementChangeMapper requirementChangeMapper;

    @Override @Transactional
    public boolean save(ProjectGroup group) {
        if (group.getMaxMembers() == null || group.getMaxMembers() <= 0) group.setMaxMembers(8);
        if (group.getInviteCode() == null || group.getInviteCode().isEmpty()) {
            String code; int tries = 0;
            do { code = generateCode(); tries++; }
            while (tries < 20 && getOne(new LambdaQueryWrapper<ProjectGroup>().eq(ProjectGroup::getInviteCode, code)) != null);
            group.setInviteCode(code);
        }
        return super.save(group);
    }

    private String generateCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
        return sb.toString();
    }

    public Map<String, Object> getGroupDetail(Long groupId) {
        ProjectGroup group = getById(groupId);
        if (group == null) return new LinkedHashMap<>();
        List<GroupMember> members = memberMapper.selectList(new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (GroupMember m : members) {
            var user = userMapper.selectById(m.getUserId());
            enriched.add(Map.of("id", m.getId(), "userId", m.getUserId(), "roleCode", m.getRoleCode(),
                "realName", user != null ? user.getRealName() : "用户" + m.getUserId()));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("group", group); result.put("members", enriched); result.put("memberCount", members.size());
        if (group.getCourseId() != null) {
            var course = courseMapper.selectById(group.getCourseId());
            result.put("courseName", course != null ? course.getCourseName() : "未分配");
            result.put("semester", course != null ? course.getSemester() : "");
        }
        return result;
    }

    @Transactional
    public GroupMember joinGroup(Long groupId, Long userId, String roleCode) {
        if (memberMapper.selectCount(new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId).eq(GroupMember::getUserId, userId)) > 0)
            throw new IllegalArgumentException("你已在该小组中");
        ProjectGroup group = getById(groupId);
        if (group == null) throw new IllegalArgumentException("小组不存在");
        if (memberMapper.selectCount(new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId)) >= group.getMaxMembers())
            throw new IllegalArgumentException("小组已满");
        GroupMember member = new GroupMember();
        member.setGroupId(groupId); member.setUserId(userId); member.setRoleCode(roleCode);
        memberMapper.insert(member); return member;
    }

    public void removeMember(Long memberId) { memberMapper.deleteById(memberId); }
    public GroupMemberMapper getMemberMapper() { return memberMapper; }

    @Transactional
    public boolean removeGroupCascade(Long groupId) {
        // 按依赖顺序级联删除所有关联数据
        // 1. 达成度分析
        achievementResultMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.analysis.entity.AchievementResult>().eq(com.obe.evaluation.analysis.entity.AchievementResult::getGroupId, groupId));
        improvementSuggestionMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.analysis.entity.ImprovementSuggestion>().eq(com.obe.evaluation.analysis.entity.ImprovementSuggestion::getObjectiveId, null)); // will be cleaned by objective cascade
        improvementTaskMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.analysis.entity.ImprovementTask>().eq(com.obe.evaluation.analysis.entity.ImprovementTask::getGroupId, groupId));
        // 2. 项目追踪
        projectMilestoneMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.project.entity.ProjectMilestone>().eq(com.obe.evaluation.project.entity.ProjectMilestone::getGroupId, groupId));
        projectTaskMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.project.entity.ProjectTask>().eq(com.obe.evaluation.project.entity.ProjectTask::getGroupId, groupId));
        gitCommitLogMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.project.entity.GitCommitLog>().eq(com.obe.evaluation.project.entity.GitCommitLog::getGroupId, groupId));
        projectJournalMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.project.entity.ProjectJournal>().eq(com.obe.evaluation.project.entity.ProjectJournal::getGroupId, groupId));
        contributionLogMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.project.entity.ContributionLog>().eq(com.obe.evaluation.project.entity.ContributionLog::getGroupId, groupId));
        // 3. 评价数据
        groupEvalMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.evaluation.entity.GroupEvaluation>().eq(com.obe.evaluation.evaluation.entity.GroupEvaluation::getGroupId, groupId));
        roleEvalMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.evaluation.entity.RoleEvaluation>().eq(com.obe.evaluation.evaluation.entity.RoleEvaluation::getGroupId, groupId));
        personalScoreMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.evaluation.entity.PersonalScore>().eq(com.obe.evaluation.evaluation.entity.PersonalScore::getGroupId, groupId));
        // 4. 问答与测试
        questionAnswerMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.qa.entity.QuestionAnswer>().eq(com.obe.evaluation.qa.entity.QuestionAnswer::getGroupId, groupId));
        // 5. Git仓库配置
        repoConfigMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.project.entity.RepoConfig>().eq(com.obe.evaluation.project.entity.RepoConfig::getGroupId, groupId));
        requirementChangeMapper.delete(new LambdaQueryWrapper<com.obe.evaluation.project.entity.RequirementChange>().eq(com.obe.evaluation.project.entity.RequirementChange::getGroupId, groupId));
        // 6. 成员
        memberMapper.delete(new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
        // 7. 小组本体
        return super.removeById(groupId);
    }
}
