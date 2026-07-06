package com.obe.evaluation.evaluation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.evaluation.entity.*;
import com.obe.evaluation.evaluation.mapper.*;
import com.obe.evaluation.group.entity.GroupMember;
import com.obe.evaluation.group.mapper.GroupMemberMapper;
import com.obe.evaluation.project.entity.ContributionLog;
import com.obe.evaluation.project.entity.GitCommitLog;
import com.obe.evaluation.project.entity.ProjectJournal;
import com.obe.evaluation.project.mapper.ContributionLogMapper;
import com.obe.evaluation.project.mapper.GitCommitLogMapper;
import com.obe.evaluation.project.mapper.ProjectJournalMapper;
import com.obe.evaluation.qa.entity.QaRecord;
import com.obe.evaluation.qa.entity.SelfTestRecord;
import com.obe.evaluation.qa.mapper.QaRecordMapper;
import com.obe.evaluation.qa.mapper.SelfTestRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 多元评价服务 — 个人成绩计算引擎
 *
 * 核心功能：
 * 1. 个人贡献度计算（反搭便车算法） — calculateContributionRatio()
 * 2. 贝叶斯平滑处理 — 小样本时向先验分布(均等贡献)收缩
 * 3. 多源数据加权融合 — Git(35%) + 日志(20%) + 问答自测(25%) + 贡献加分(20%)
 * 4. 角色评价加分 — calculateBonus()
 *
 * 最终成绩公式：finalScore = groupTotalScore × contributionRatio + bonusTotal
 *
 * @see 论文第四章 4.1 贝叶斯平滑贡献度算法
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final GroupEvaluationMapper groupEvalMapper;
    private final RoleEvaluationMapper roleEvalMapper;
    private final PersonalScoreMapper scoreMapper;
    private final GroupMemberMapper memberMapper;
    private final GitCommitLogMapper gitCommitMapper;
    private final ProjectJournalMapper journalMapper;
    private final ContributionLogMapper contributionMapper;
    private final QaRecordMapper qaRecordMapper;
    private final SelfTestRecordMapper selfTestMapper;

    /**
     * 计算单个学生的个人最终成绩
     * 公式：最终成绩 = 小组成绩 × 贡献系数 + 个人加分
     * 步骤：
     *   1. 取该小组所有评价维度的均分作为小组总分
     *   2. 通过 calculateContributionRatio() 计算个人贡献系数（反搭便车）
     *   3. 通过 calculateBonus() 计算角色评价加分
     *   4. 组合计算并持久化到 personal_score 表
     */
    @Transactional
    public PersonalScore calculatePersonalScore(Long userId, Long groupId) {
        // 1. Get group total score from group evaluations
        List<GroupEvaluation> groupEvals = groupEvalMapper.selectList(
                new LambdaQueryWrapper<GroupEvaluation>().eq(GroupEvaluation::getGroupId, groupId));
        double groupTotalScore = 0;
        if (!groupEvals.isEmpty()) {
            groupTotalScore = groupEvals.stream()
                    .mapToDouble(e -> e.getScore() != null ? e.getScore() : 0)
                    .average()
                    .orElse(0);
        } else {
            groupTotalScore = 80.0; // default
        }

        // 2. Calculate contribution ratio (anti-freerider algorithm)
        double contributionRatio = calculateContributionRatio(userId, groupId);

        // 3. Calculate bonus total from contributions and role evaluations
        double bonusTotal = calculateBonus(userId, groupId);

        // 4. Assemble final score
        double finalScore = groupTotalScore * contributionRatio + bonusTotal;
        finalScore = Math.max(0, Math.min(100, finalScore));

        // 5. Save
        PersonalScore existing = scoreMapper.selectOne(
                new LambdaQueryWrapper<PersonalScore>()
                        .eq(PersonalScore::getUserId, userId)
                        .eq(PersonalScore::getGroupId, groupId));
        if (existing == null) {
            existing = new PersonalScore();
            existing.setUserId(userId);
            existing.setGroupId(groupId);
        }
        existing.setGroupTotalScore(Math.round(groupTotalScore * 100.0) / 100.0);
        existing.setContributionRatio(Math.round(contributionRatio * 10000.0) / 10000.0);
        existing.setBonusTotal(Math.round(bonusTotal * 100.0) / 100.0);
        existing.setFinalScore(Math.round(finalScore * 100.0) / 100.0);
        existing.setCalculatedAt(LocalDateTime.now());

        if (existing.getId() != null) {
            scoreMapper.updateById(existing);
        } else {
            scoreMapper.insert(existing);
        }
        return existing;
    }

    /**
     * Batch calculate for all members in a group.
     */
    @Transactional
    public Map<String, Object> calculateBatch(Long groupId) {
        List<GroupMember> members = memberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
        int success = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();

        for (GroupMember member : members) {
            try {
                calculatePersonalScore(member.getUserId(), groupId);
                success++;
            } catch (Exception e) {
                log.error("Failed to calculate score for userId={} groupId={}: {}", member.getUserId(), groupId, e.getMessage());
                failed++;
                errors.add("userId=" + member.getUserId() + ": " + e.getMessage());
            }
        }

        return Map.of("groupId", groupId, "totalMembers", members.size(),
                "success", success, "failed", failed, "errors", errors);
    }

    /**
     * 【核心算法】反搭便车贡献度计算
     *
     * 多源数据加权融合：
     *   贡献分 = Git得分×0.35 + 日志得分×0.20 + (问答分+自测分)×0.25 + 贡献加分×0.20
     *
     * 贝叶斯平滑公式：
     *   r_i = (S_i + C × prior) / (S_max + C)
     *   其中 prior = 1/n (n为小组成员数), C = 50 (平滑因子)
     *
     * 设计意图：
     *   - 当数据稀疏时 → 向均等贡献收缩（宽容，避免误伤）
     *   - 当数据充足时 → 反映真实贡献差异（精准区分）
     *
     * @see 论文第四章 4.1 贝叶斯平滑贡献度算法
     */
    private double calculateContributionRatio(Long userId, Long groupId) {
        List<GroupMember> members = memberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
        if (members.size() <= 1) return 1.0;

        Map<Long, Double> contributionScores = new HashMap<>();
        for (GroupMember m : members) {
            Long uid = m.getUserId();

            // Git commits: 5 base points per commit + additions + deletions
            List<GitCommitLog> commits = gitCommitMapper.selectList(
                    new LambdaQueryWrapper<GitCommitLog>()
                            .eq(GitCommitLog::getUserId, uid)
                            .eq(GitCommitLog::getGroupId, groupId));
            double gitScore = 0;
            for (GitCommitLog c : commits) {
                int adds = c.getAdditions() != null ? c.getAdditions() : 0;
                int dels = c.getDeletions() != null ? c.getDeletions() : 0;
                gitScore += 5 + Math.max(adds, 0) + Math.max(dels, 0);
            }

            // Journals: 15 per entry
            List<ProjectJournal> journals = journalMapper.selectList(
                    new LambdaQueryWrapper<ProjectJournal>()
                            .eq(ProjectJournal::getUserId, uid)
                            .eq(ProjectJournal::getGroupId, groupId));
            double journalScore = journals.size() * 15.0;

            // AI问答活跃度（P0-1：主动学习参与度）
            long qaCount = qaRecordMapper.selectCount(
                    new LambdaQueryWrapper<QaRecord>().eq(QaRecord::getUserId, uid));
            List<SelfTestRecord> tests = selfTestMapper.selectList(
                    new LambdaQueryWrapper<SelfTestRecord>().eq(SelfTestRecord::getUserId, uid));
            double qaScore = qaCount * 3.0;  // 每次提问3分
            double testScore = 0;
            for (SelfTestRecord t : tests) {
                if (t.getTotal() != null && t.getTotal() > 0) {
                    testScore += (t.getScore() / t.getTotal()) * 15;  // 自测满分15分
                }
            }

            // Approved contributions
            List<ContributionLog> contributions = contributionMapper.selectList(
                    new LambdaQueryWrapper<ContributionLog>()
                            .eq(ContributionLog::getUserId, uid)
                            .eq(ContributionLog::getGroupId, groupId)
                            .eq(ContributionLog::getApproved, true));
            double bonusSum = 0;
            for (ContributionLog cl : contributions) {
                bonusSum += cl.getBonusScore() != null ? cl.getBonusScore() : 0;
            }

            // 权重分配：Git 35% + 日志 20% + 问答自测 25% + 贡献加分 20%
            double total = gitScore * 0.35 + journalScore * 0.20
                + (qaScore + testScore) * 0.25 + bonusSum * 0.20;
            contributionScores.put(uid, total);
        }

        double myScore = contributionScores.getOrDefault(userId, 0.0);
        double maxScore = contributionScores.values().stream().mapToDouble(v -> v).max().orElse(1.0);

        if (maxScore <= 0) return 1.0;

        // Bayesian smoothing
        int memberCount = members.size();
        double prior = 1.0 / memberCount;
        double C = 50;
        double ratio = (myScore + C * prior) / (maxScore + C);
        ratio = Math.max(0.01, Math.min(1.0, ratio));
        return ratio;
    }

    /**
     * Calculate bonus from role evaluations.
     */
    private double calculateBonus(Long userId, Long groupId) {
        List<RoleEvaluation> roleEvals = roleEvalMapper.selectList(
                new LambdaQueryWrapper<RoleEvaluation>()
                        .eq(RoleEvaluation::getUserId, userId)
                        .eq(RoleEvaluation::getGroupId, groupId));
        if (roleEvals.isEmpty()) return 0;

        double avgScore = roleEvals.stream()
                .mapToDouble(e -> e.getScore() != null ? e.getScore() : 0)
                .average()
                .orElse(0);

        // Bonus: each point above 60 gets 0.05 bonus (max 2.0)
        double bonus = Math.max(0, (avgScore - 60) * 0.05);
        return Math.min(5.0, bonus);
    }
}
