package com.obe.evaluation.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.common.R;
import com.obe.evaluation.evaluation.entity.PersonalScore;
import com.obe.evaluation.evaluation.mapper.PersonalScoreMapper;
import com.obe.evaluation.group.entity.GroupMember;
import com.obe.evaluation.analysis.entity.AchievementResult;
import com.obe.evaluation.analysis.mapper.AchievementResultMapper;
import com.obe.evaluation.course.entity.CourseObjective;
import com.obe.evaluation.course.mapper.CourseObjectiveMapper;
import com.obe.evaluation.group.mapper.GroupMemberMapper;
import com.obe.evaluation.group.mapper.ProjectGroupMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Tag(name = "学生接口")
public class StudentController {

    private final PersonalScoreMapper scoreMapper;
    private final GroupMemberMapper memberMapper;
    private final ProjectGroupMapper groupMapper;
    private final AchievementResultMapper achievementResultMapper;
    private final CourseObjectiveMapper objectiveMapper;

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long id))
            throw new IllegalArgumentException("未登录");
        return id;
    }

    @GetMapping("/scores")
    @Operation(summary = "获取当前学生的成绩摘要")
    public R<Map<String, Object>> myScores() {
        Long userId = currentUserId();

        // Get all personal scores
        List<PersonalScore> scores = scoreMapper.selectList(
                new LambdaQueryWrapper<PersonalScore>()
                        .eq(PersonalScore::getUserId, userId)
                        .orderByDesc(PersonalScore::getCalculatedAt));

        if (scores.isEmpty()) {
            return R.ok(Map.of("overallAchievement", 0.0, "finalScore", 0.0));
        }

        // Get the latest/most relevant score
        PersonalScore latest = scores.get(0);

        // 获取该学生所有小组的成绩概要
        double totalAchievement = 0;
        int count = 0;
        for (PersonalScore s : scores) {
            if (s.getFinalScore() != null) {
                totalAchievement += s.getFinalScore() / 100.0;
                count++;
            }
        }
        double overallAchievement = count > 0 ? Math.round(totalAchievement / count * 10000.0) / 10000.0 : 0;

        // 从达成度结果获取最新轮次的课程目标详情
        List<Map<String, Object>> objectiveDetails = new ArrayList<>();
        try {
            // 找到最新calcRound
            var results = achievementResultMapper.selectList(
                new LambdaQueryWrapper<AchievementResult>()
                    .eq(AchievementResult::getGroupId, latest.getGroupId())
                    .orderByDesc(AchievementResult::getCalcRound)
                    .last("LIMIT 1"));
            Integer latestRound = results.isEmpty() ? null : results.get(0).getCalcRound();

            var achievementResults = achievementResultMapper.selectList(
                new LambdaQueryWrapper<AchievementResult>()
                    .eq(AchievementResult::getGroupId, latest.getGroupId())
                    .eq(latestRound != null, AchievementResult::getCalcRound, latestRound)
                    .orderByAsc(AchievementResult::getObjectiveId));
            for (var ar : achievementResults) {
                var obj = objectiveMapper.selectById(ar.getObjectiveId());
                Map<String, Object> detail = new LinkedHashMap<>();
                detail.put("title", obj != null ? obj.getTitle() : "目标#" + ar.getObjectiveId());
                detail.put("dimension", obj != null ? obj.getDimension() : "KNOWLEDGE");
                detail.put("weight", obj != null && obj.getWeight() != null ? obj.getWeight() : 0);
                detail.put("achievement", ar.getAchievementValue() != null ? ar.getAchievementValue() : 0);
                objectiveDetails.add(detail);
            }
        } catch (Exception e) { log.debug("Achievement detail enrichment skipped: {}", e.getMessage()); }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("overallAchievement", overallAchievement);
        result.put("finalScore", latest.getFinalScore());
        result.put("groupScore", latest.getGroupTotalScore());
        result.put("contributionRatio", latest.getContributionRatio());
        result.put("bonus", latest.getBonusTotal());
        result.put("objectiveDetails", objectiveDetails);

        return R.ok(result);
    }
}
