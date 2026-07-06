package com.obe.evaluation.analysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.analysis.entity.AchievementResult;
import com.obe.evaluation.analysis.entity.ImprovementSuggestion;
import com.obe.evaluation.analysis.mapper.AchievementResultMapper;
import com.obe.evaluation.analysis.mapper.ImprovementSuggestionMapper;
import com.obe.evaluation.course.entity.CourseObjective;
import com.obe.evaluation.course.entity.EvaluationMethod;
import com.obe.evaluation.course.entity.GraduationIndicator;
import com.obe.evaluation.course.mapper.CourseObjectiveMapper;
import com.obe.evaluation.course.mapper.EvaluationMethodMapper;
import com.obe.evaluation.course.mapper.GraduationIndicatorMapper;
import com.obe.evaluation.evaluation.entity.GroupEvaluation;
import com.obe.evaluation.evaluation.entity.PersonalScore;
import com.obe.evaluation.evaluation.entity.RoleEvaluation;
import com.obe.evaluation.evaluation.mapper.GroupEvaluationMapper;
import com.obe.evaluation.evaluation.mapper.PersonalScoreMapper;
import com.obe.evaluation.evaluation.mapper.RoleEvaluationMapper;
import com.obe.evaluation.group.entity.GroupMember;
import com.obe.evaluation.group.entity.ProjectGroup;
import com.obe.evaluation.group.mapper.GroupMemberMapper;
import com.obe.evaluation.group.mapper.ProjectGroupMapper;
import com.obe.evaluation.project.entity.GitCommitLog;
import com.obe.evaluation.project.entity.ProjectJournal;
import com.obe.evaluation.project.mapper.GitCommitLogMapper;
import com.obe.evaluation.project.mapper.ProjectJournalMapper;
import com.obe.evaluation.qa.entity.SelfTestRecord;
import com.obe.evaluation.qa.mapper.SelfTestRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final CourseObjectiveMapper objectiveMapper;
    private final GraduationIndicatorMapper indicatorMapper;
    private final EvaluationMethodMapper methodMapper;
    private final AchievementResultMapper resultMapper;
    private final ImprovementSuggestionMapper suggestionMapper;
    private final GroupEvaluationMapper groupEvalMapper;
    private final RoleEvaluationMapper roleEvalMapper;
    private final PersonalScoreMapper scoreMapper;
    private final GroupMemberMapper memberMapper;
    private final ProjectGroupMapper groupMapper;
    private final GitCommitLogMapper gitCommitMapper;
    private final ProjectJournalMapper journalMapper;
    private final SelfTestRecordMapper testMapper;

    /**
     * Calculate achievement for all objectives in a course.
     * For each objective -> each indicator -> each evaluation method:
     *   fetch actual scores based on dataSource, then compute weighted achievement.
     */
    @Transactional
    public Map<String, Object> calculate(Long courseId, Long groupId) {
        List<CourseObjective> objectives = objectiveMapper.selectList(
                new LambdaQueryWrapper<CourseObjective>()
                        .eq(CourseObjective::getCourseId, courseId)
                        .orderByAsc(CourseObjective::getSortOrder));

        if (objectives.isEmpty()) {
            return Map.of("message", "该课程没有课程目标", "courseId", courseId);
        }

        // Determine the calc round
        Integer maxRound = getMaxCalcRound(courseId, groupId);
        int calcRound = (maxRound != null ? maxRound : 0) + 1;

        int totalCalculated = 0;
        List<Map<String, Object>> details = new ArrayList<>();

        for (CourseObjective obj : objectives) {
            List<GraduationIndicator> indicators = indicatorMapper.selectList(
                    new LambdaQueryWrapper<GraduationIndicator>()
                            .eq(GraduationIndicator::getObjectiveId, obj.getId())
                            .orderByAsc(GraduationIndicator::getSortOrder));

            double weightedSum = 0;
            double totalWeight = 0;
            int indicatorCount = 0;

            for (GraduationIndicator ind : indicators) {
                List<EvaluationMethod> methods = methodMapper.selectList(
                        new LambdaQueryWrapper<EvaluationMethod>()
                                .eq(EvaluationMethod::getIndicatorId, ind.getId()));

                double indicatorAchievement = 0;
                double methodTotalWeight = 0;

                for (EvaluationMethod method : methods) {
                    double actualScore = fetchActualScore(method, courseId, groupId,
                            obj.getDimension() != null ? obj.getDimension() : "GENERAL");
                    double fullScore = method.getFullScore() != null ? method.getFullScore() : 100.0;
                    if (fullScore <= 0) fullScore = 100.0;

                    double methodAchievement = actualScore / fullScore;
                    double mWeight = method.getWeight() != null ? method.getWeight() : 0;

                    if (mWeight > 0) {
                        indicatorAchievement += methodAchievement * mWeight;
                        methodTotalWeight += mWeight;
                    }
                }

                if (methodTotalWeight > 0) {
                    indicatorAchievement = indicatorAchievement / methodTotalWeight;
                } else {
                    indicatorAchievement = 0;
                }

                double iWeight = 1.0; // equal weight for indicators within an objective
                weightedSum += indicatorAchievement * iWeight;
                totalWeight += iWeight;
                indicatorCount++;
            }

            double objectiveAchievement = totalWeight > 0 ? weightedSum / totalWeight : 0;
            objectiveAchievement = Math.max(0, Math.min(1, objectiveAchievement));

            // Save achievement result
            AchievementResult result = new AchievementResult();
            result.setObjectiveId(obj.getId());
            result.setGroupId(groupId);
            result.setAchievementValue(Math.round(objectiveAchievement * 10000.0) / 10000.0);
            result.setDimension(obj.getDimension() != null ? obj.getDimension() : "GENERAL");
            result.setCalcRound(calcRound);
            result.setCalculatedAt(LocalDateTime.now());
            resultMapper.insert(result);

            details.add(Map.of(
                    "objectiveId", obj.getId(),
                    "title", obj.getTitle(),
                    "achievement", result.getAchievementValue(),
                    "indicatorCount", indicatorCount
            ));
            totalCalculated++;
        }

        return Map.of(
                "courseId", courseId,
                "groupId", groupId,
                "calcRound", calcRound,
                "totalCalculated", totalCalculated,
                "details", details
        );
    }

    /**
     * Fetch actual score for a given evaluation method based on its dataSource.
     */
    private double fetchActualScore(EvaluationMethod method, Long courseId, Long groupId, String dimension) {
        String dataSource = method.getDataSource() != null ? method.getDataSource().toLowerCase() : "group_eval";
        String evalType = method.getEvalType() != null ? method.getEvalType() : "";

        try {
            switch (dataSource) {
                case "group_eval":
                    return fetchGroupEvaluationScore(groupId, dimension);
                case "role_eval":
                    return fetchRoleEvaluationScore(groupId, evalType);
                case "git":
                    return fetchGitScore(groupId);
                case "journal":
                    return fetchJournalScore(groupId);
                case "self_test":
                    return fetchSelfTestScore(groupId);
                case "personal_score":
                    return fetchPersonalScoreAverage(groupId);
                default:
                    return fetchGroupEvaluationScore(groupId, dimension);
            }
        } catch (Exception e) {
            log.warn("Error fetching score for method {} (source={}): {}",
                    method.getMethodName(), dataSource, e.getMessage());
            return 0;
        }
    }

    private double fetchGroupEvaluationScore(Long groupId, String dimension) {
        if (groupId == null) return 0;
        // 取所有维度的小组评价均分（CODE/DESIGN/DOC/TEST/PRESENTATION/REQUIREMENT）
        // 不按OBE维度过滤——group_evaluation的dimension是评价方面，不是OBE维度
        var wq = new LambdaQueryWrapper<GroupEvaluation>()
                .eq(GroupEvaluation::getGroupId, groupId);
        List<GroupEvaluation> evals = groupEvalMapper.selectList(wq);
        if (evals.isEmpty()) return 0;
        return evals.stream()
                .mapToDouble(e -> e.getScore() != null ? e.getScore() : 0)
                .average().orElse(0);
    }

    private double fetchRoleEvaluationScore(Long groupId, String roleCode) {
        if (groupId == null) return 0;
        var wq = new LambdaQueryWrapper<RoleEvaluation>()
                .eq(RoleEvaluation::getGroupId, groupId);
        if (roleCode != null && !roleCode.isEmpty()) {
            wq.eq(RoleEvaluation::getRoleCode, roleCode);
        }
        List<RoleEvaluation> evals = roleEvalMapper.selectList(wq);
        if (evals.isEmpty()) return 0;
        return evals.stream()
                .mapToDouble(e -> e.getScore() != null ? e.getScore() : 0)
                .average().orElse(0);
    }

    private double fetchGitScore(Long groupId) {
        if (groupId == null) return 0;
        List<GitCommitLog> commits = gitCommitMapper.selectList(
                new LambdaQueryWrapper<GitCommitLog>()
                        .eq(GitCommitLog::getGroupId, groupId));
        if (commits.isEmpty()) return 0;

        // Use addition count as proxy score, scaled to 0-100
        long totalAdditions = 0;
        int memberCount = 1;
        var members = memberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
        if (members != null && !members.isEmpty()) memberCount = members.size();

        for (GitCommitLog c : commits) {
            totalAdditions += c.getAdditions() != null ? Math.max(0, c.getAdditions()) : 0;
        }

        // Scale: 500 additions per member = 80 points
        double expectedAdditions = 500.0 * memberCount;
        return Math.min(100, (totalAdditions / Math.max(1, expectedAdditions)) * 80);
    }

    private double fetchJournalScore(Long groupId) {
        if (groupId == null) return 0;
        List<ProjectJournal> journals = journalMapper.selectList(
                new LambdaQueryWrapper<ProjectJournal>()
                        .eq(ProjectJournal::getGroupId, groupId));
        if (journals.isEmpty()) return 0;

        var members = memberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
        int memberCount = members != null && !members.isEmpty() ? members.size() : 1;

        // Scale: 5 journals per member = 80 points
        double expectedJournals = 5.0 * memberCount;
        return Math.min(100, (journals.size() / Math.max(1, expectedJournals)) * 80);
    }

    private double fetchSelfTestScore(Long groupId) {
        if (groupId == null) return 0;
        // Get members, then get their test scores
        var members = memberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
        if (members == null || members.isEmpty()) return 0;

        double totalScore = 0;
        int count = 0;
        for (GroupMember m : members) {
            List<SelfTestRecord> tests = testMapper.selectList(
                    new LambdaQueryWrapper<SelfTestRecord>()
                            .eq(SelfTestRecord::getUserId, m.getUserId())
                            .orderByDesc(SelfTestRecord::getTakenAt)
                            .last("LIMIT 1"));
            if (!tests.isEmpty()) {
                SelfTestRecord latest = tests.get(0);
                if (latest.getTotal() != null && latest.getTotal() > 0 && latest.getScore() != null) {
                    totalScore += (latest.getScore() / latest.getTotal()) * 100;
                    count++;
                }
            }
        }
        return count > 0 ? totalScore / count : 0;
    }

    private double fetchPersonalScoreAverage(Long groupId) {
        if (groupId == null) return 0;
        List<PersonalScore> scores = scoreMapper.selectList(
                new LambdaQueryWrapper<PersonalScore>()
                        .eq(PersonalScore::getGroupId, groupId));
        if (scores.isEmpty()) return 0;
        return scores.stream()
                .mapToDouble(s -> s.getFinalScore() != null ? s.getFinalScore() : 0)
                .average().orElse(0);
    }

    private Integer getMaxCalcRound(Long courseId, Long groupId) {
        List<AchievementResult> results = resultMapper.selectList(
                new LambdaQueryWrapper<AchievementResult>()
                        .eq(groupId != null, AchievementResult::getGroupId, groupId));
        if (results.isEmpty()) return null;

        // Filter by objectives belonging to this course
        List<Long> objectiveIds = objectiveMapper.selectList(
                        new LambdaQueryWrapper<CourseObjective>()
                                .eq(CourseObjective::getCourseId, courseId)
                                .select(CourseObjective::getId))
                .stream().map(CourseObjective::getId).toList();

        return results.stream()
                .filter(r -> objectiveIds.contains(r.getObjectiveId()))
                .mapToInt(r -> r.getCalcRound() != null ? r.getCalcRound() : 0)
                .max().orElse(0);
    }

    /**
     * Get per-student achievement for a course.
     */
    public List<Map<String, Object>> getStudentAchievement(Long courseId, Long groupId) {
        List<Map<String, Object>> result = new ArrayList<>();

        // Get the groups for this course
        var groupWq = new LambdaQueryWrapper<ProjectGroup>();
        if (groupId != null) {
            groupWq.eq(ProjectGroup::getId, groupId);
        }
        groupWq.eq(ProjectGroup::getCourseId, courseId);
        List<ProjectGroup> groups = groupMapper.selectList(groupWq);

        Set<Long> processedUsers = new HashSet<>();

        for (ProjectGroup group : groups) {
            List<GroupMember> members = memberMapper.selectList(
                    new LambdaQueryWrapper<GroupMember>()
                            .eq(GroupMember::getGroupId, group.getId()));
            for (GroupMember member : members) {
                if (processedUsers.contains(member.getUserId())) continue;
                processedUsers.add(member.getUserId());

                PersonalScore score = scoreMapper.selectOne(
                        new LambdaQueryWrapper<PersonalScore>()
                                .eq(PersonalScore::getUserId, member.getUserId())
                                .eq(PersonalScore::getGroupId, group.getId()));

                // Get achievement for objectives
                var achievements = resultMapper.selectList(
                        new LambdaQueryWrapper<AchievementResult>()
                                .eq(AchievementResult::getGroupId, group.getId()));

                Map<String, Object> studentData = new LinkedHashMap<>();
                studentData.put("userId", member.getUserId());
                studentData.put("groupId", group.getId());
                studentData.put("groupName", group.getGroupName() != null ? group.getGroupName() : "");
                studentData.put("roleCode", member.getRoleCode());
                studentData.put("finalScore", score != null ? score.getFinalScore() : null);
                studentData.put("contributionRatio", score != null ? score.getContributionRatio() : null);
                studentData.put("achievements", achievements);

                // Calculate weighted achievement
                if (!achievements.isEmpty()) {
                    double weightedAch = 0;
                    double totalW = 0;
                    for (AchievementResult ar : achievements) {
                        var obj = objectiveMapper.selectById(ar.getObjectiveId());
                        double w = (obj != null && obj.getWeight() != null) ? obj.getWeight() : 0;
                        double ach = ar.getAchievementValue() != null ? ar.getAchievementValue() : 0;
                        if (w > 0) {
                            weightedAch += ach * w;
                            totalW += w;
                        }
                    }
                    studentData.put("weightedAchievement", totalW > 0
                            ? Math.round(weightedAch / totalW * 10000.0) / 10000.0 : 0);
                } else {
                    studentData.put("weightedAchievement", 0);
                }

                result.add(studentData);
            }
        }

        return result;
    }

    /**
     * Auto-generate improvement suggestions based on low achievement.
     * When groupId is provided, suggestions are based on that group's data only.
     */
    @Transactional
    public List<ImprovementSuggestion> generateSuggestions(Long courseId) {
        return generateSuggestions(courseId, null);
    }

    @Transactional
    public List<ImprovementSuggestion> generateSuggestions(Long courseId, Long groupId) {
        List<ImprovementSuggestion> generated = new ArrayList<>();
        List<CourseObjective> objectives = objectiveMapper.selectList(
                new LambdaQueryWrapper<CourseObjective>()
                        .eq(CourseObjective::getCourseId, courseId));

        for (CourseObjective obj : objectives) {
            var query = new LambdaQueryWrapper<AchievementResult>()
                    .eq(AchievementResult::getObjectiveId, obj.getId());
            if (groupId != null) {
                query.eq(AchievementResult::getGroupId, groupId);
            }
            var results = resultMapper.selectList(query);

            double avgAchievement = 0;
            if (!results.isEmpty()) {
                avgAchievement = results.stream()
                        .mapToDouble(r -> r.getAchievementValue() != null ? r.getAchievementValue() : 0)
                        .average().orElse(0);
            }

            // Delete old auto suggestions for this objective
            suggestionMapper.delete(
                    new LambdaQueryWrapper<ImprovementSuggestion>()
                            .eq(ImprovementSuggestion::getObjectiveId, obj.getId())
                            .eq(ImprovementSuggestion::getIsAuto, true));

            // Generate new suggestion
            ImprovementSuggestion sug = new ImprovementSuggestion();
            sug.setObjectiveId(obj.getId());
            sug.setIsAuto(true);
            sug.setCreatedAt(LocalDateTime.now());

            if (avgAchievement < 0.6) {
                sug.setSuggestion("【" + obj.getTitle() + "】达成度较低（"
                        + Math.round(avgAchievement * 100) + "%），建议：\n"
                        + "1. 增加该目标相关的小组实践活动\n"
                        + "2. 补充针对性练习和测试\n"
                        + "3. 调整教学方法和评价方式\n"
                        + "4. 组织专题讨论和答疑");
            } else if (avgAchievement < 0.8) {
                sug.setSuggestion("【" + obj.getTitle() + "】达成度中等（"
                        + Math.round(avgAchievement * 100) + "%），建议：\n"
                        + "1. 强化薄弱指标的教学\n"
                        + "2. 增加实战项目训练");
            } else if (avgAchievement > 0) {
                sug.setSuggestion("【" + obj.getTitle() + "】达成度良好（"
                        + Math.round(avgAchievement * 100) + "%），继续保持当前教学策略。");
            } else {
                sug.setSuggestion("【" + obj.getTitle() + "】暂无达成度数据，建议尽快开展评价。");
            }

            suggestionMapper.insert(sug);
            generated.add(sug);

            // Also generate per-indicator suggestions
            List<GraduationIndicator> indicators = indicatorMapper.selectList(
                    new LambdaQueryWrapper<GraduationIndicator>()
                            .eq(GraduationIndicator::getObjectiveId, obj.getId()));
            for (GraduationIndicator ind : indicators) {
                // Check if this indicator has methods with low scores
                List<EvaluationMethod> methods = methodMapper.selectList(
                        new LambdaQueryWrapper<EvaluationMethod>()
                                .eq(EvaluationMethod::getIndicatorId, ind.getId()));
                if (!methods.isEmpty() && methods.stream().allMatch(
                        m -> m.getWeight() == null || m.getWeight() <= 0.1)) {
                    ImprovementSuggestion indSug = new ImprovementSuggestion();
                    indSug.setObjectiveId(obj.getId());
                    indSug.setIndicatorId(ind.getId());
                    indSug.setIsAuto(true);
                    indSug.setCreatedAt(LocalDateTime.now());
                    indSug.setSuggestion("指标【" + ind.getTitle() + "】评价方法权重偏低，建议调整。");
                    suggestionMapper.insert(indSug);
                    generated.add(indSug);
                }
            }
        }

        return generated;
    }
}
