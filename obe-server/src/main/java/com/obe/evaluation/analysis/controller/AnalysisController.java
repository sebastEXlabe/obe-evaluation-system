/**
 * 达成度分析控制器 — OBE闭环核心
 * 提供课程目标达成度、PO毕业要求达成度、AHP权重校验、
 * PDCA改进工单、成绩回溯反查等功能
 */
package com.obe.evaluation.analysis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.analysis.entity.*;
import com.obe.evaluation.analysis.mapper.*;
import com.obe.evaluation.analysis.entity.ImprovementTask;
import com.obe.evaluation.analysis.mapper.ImprovementTaskMapper;
import com.obe.evaluation.analysis.service.AchievementService;
import com.obe.evaluation.analysis.service.AhpService;
import com.obe.evaluation.common.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obe.evaluation.course.entity.CourseObjective;
import com.obe.evaluation.course.entity.EvaluationMethod;
import com.obe.evaluation.course.entity.GraduationIndicator;
import com.obe.evaluation.course.mapper.CourseObjectiveMapper;
import com.obe.evaluation.course.mapper.GraduationRequirementMapper;
import com.obe.evaluation.course.mapper.ObjectiveRequirementMappingMapper;
import com.obe.evaluation.evaluation.entity.GroupEvaluation;
import com.obe.evaluation.group.mapper.ProjectGroupMapper;
import com.obe.evaluation.project.entity.GitCommitLog;
import com.obe.evaluation.project.entity.ProjectJournal;
import com.obe.evaluation.qa.entity.SelfTestRecord;
import com.obe.evaluation.system.entity.Course;
import com.obe.evaluation.system.mapper.CourseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController @RequestMapping("/api/analysis") @RequiredArgsConstructor
@Tag(name = "达成度分析")
public class AnalysisController {
    private final AchievementService achievementService;
    private final AhpService ahpService;
    private final AchievementResultMapper resultMapper;
    private final ImprovementSuggestionMapper suggestionMapper;
    private final CourseObjectiveMapper objectiveMapper;
    private final ProjectGroupMapper groupMapper;
    private final GraduationRequirementMapper grMapper;
    private final ObjectiveRequirementMappingMapper mappingMapper;
    private final ImprovementTaskMapper taskMapper;
    private final com.obe.evaluation.course.mapper.GraduationIndicatorMapper indicatorMapper;
    private final com.obe.evaluation.course.mapper.EvaluationMethodMapper methodMapper;
    private final CourseMapper courseMapper;
    private final com.obe.evaluation.project.mapper.GitCommitLogMapper gitCommitMapper;
    private final com.obe.evaluation.project.mapper.ProjectJournalMapper journalMapper;
    private final com.obe.evaluation.evaluation.mapper.GroupEvaluationMapper groupEvalMapper;
    private final com.obe.evaluation.qa.mapper.SelfTestRecordMapper selfTestMapper;
    private final com.obe.evaluation.group.mapper.GroupMemberMapper groupMemberMapper;
    private final com.obe.evaluation.system.mapper.SysUserMapper userMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long resolveCourseId(Long groupId) {
        if (groupId == null) return null;
        var g = groupMapper.selectById(groupId);
        return g != null ? g.getCourseId() : null;
    }

    @PostMapping("/calculate")
    @Operation(summary = "计算达成度（传groupId自动解析courseId）")
    public R<Map<String, Object>> calculate(@RequestParam Long groupId) {
        Long courseId = resolveCourseId(groupId);
        if (courseId == null) return R.fail(400, "小组未关联课程");
        return R.ok(achievementService.calculate(courseId, groupId));
    }

    @GetMapping("/achievement")
    @Operation(summary = "获取达成度概览（传groupId自动解析courseId）")
    public R<Map<String, Object>> achievement(@RequestParam Long groupId) {
        Long courseId = resolveCourseId(groupId);
        if (courseId == null) return R.fail(400, "小组未关联课程");
        List<CourseObjective> objs = objectiveMapper.selectList(
            new LambdaQueryWrapper<CourseObjective>().eq(CourseObjective::getCourseId, courseId).orderByAsc(CourseObjective::getSortOrder));

        // 过滤掉没有指标点的空目标（测试数据等）
        List<CourseObjective> validObjs = new ArrayList<>();
        for (CourseObjective obj : objs) {
            long indicatorCount = indicatorMapper.selectCount(
                new LambdaQueryWrapper<GraduationIndicator>().eq(GraduationIndicator::getObjectiveId, obj.getId()));
            if (indicatorCount > 0) validObjs.add(obj);
        }
        // 如果没有带指标点的目标，保留全部目标
        if (validObjs.isEmpty()) validObjs = objs;

        // 归一化权重：确保权重之和为1.0
        double rawSum = validObjs.stream().mapToDouble(o -> o.getWeight() != null ? o.getWeight() : 0).sum();
        double weightScale = Math.abs(rawSum - 1.0) < 0.001 ? 1.0 : (rawSum > 0 ? 1.0 / rawSum : 1.0);

        List<Map<String, Object>> details = new ArrayList<>();
        double totalWeightedAchievement = 0;
        double totalWeight = 0;
        Map<String, Double> dimWeightedSum = new LinkedHashMap<>(Map.of("KNOWLEDGE", 0.0, "ABILITY", 0.0, "QUALITY", 0.0));
        Map<String, Double> dimTotalWeight = new LinkedHashMap<>(Map.of("KNOWLEDGE", 0.0, "ABILITY", 0.0, "QUALITY", 0.0));

        for (CourseObjective obj : validObjs) {
            var results = resultMapper.selectList(new LambdaQueryWrapper<AchievementResult>()
                .eq(AchievementResult::getObjectiveId, obj.getId()).eq(AchievementResult::getGroupId, groupId));
            double avg = results.isEmpty() ? 0 : results.stream()
                .mapToDouble(r -> r.getAchievementValue() != null ? r.getAchievementValue() : 0)
                .average().orElse(0);
            double rawWeight = obj.getWeight() != null ? obj.getWeight() : 0;
            double normalizedWeight = rawWeight * weightScale;

            details.add(Map.of("id", obj.getId(), "title", obj.getTitle(), "dimension", obj.getDimension(),
                "weight", Math.round(rawWeight * 10000.0) / 10000.0,
                "normalizedWeight", Math.round(normalizedWeight * 10000.0) / 10000.0,
                "achievement", Math.round(avg * 10000.0) / 10000.0));

            if (rawWeight > 0) {
                totalWeightedAchievement += avg * normalizedWeight;
                totalWeight += normalizedWeight;
                String dim = obj.getDimension() != null ? obj.getDimension() : "KNOWLEDGE";
                dimWeightedSum.merge(dim, avg * normalizedWeight, Double::sum);
                dimTotalWeight.merge(dim, normalizedWeight, Double::sum);
            }
        }
        double overall = totalWeight > 0 ? Math.round(totalWeightedAchievement / totalWeight * 10000.0) / 10000.0 : 0;

        Map<String, Double> dimScores = new LinkedHashMap<>();
        for (String dim : List.of("KNOWLEDGE", "ABILITY", "QUALITY")) {
            double dwSum = dimWeightedSum.getOrDefault(dim, 0.0);
            double dwTotal = dimTotalWeight.getOrDefault(dim, 0.0);
            dimScores.put(dim.toLowerCase(), dwTotal > 0 ? Math.round(dwSum / dwTotal * 100.0) / 100.0 : 0);
        }

        return R.ok(Map.of("overallAchievement", overall, "objectiveDetails", details,
            "dimensionScores", Map.of("knowledge", dimScores.get("knowledge"),
                "ability", dimScores.get("ability"), "quality", dimScores.get("quality")),
            "weightNormalized", Math.abs(rawSum - 1.0) >= 0.001,
            "originalWeightSum", Math.round(rawSum * 100.0) / 100.0));
    }

    @GetMapping("/ahp-check")
    @Operation(summary = "AHP权重校验（传groupId自动解析courseId，使用真实两两比较矩阵+CI/CR一致性检验）")
    public R<Map<String, Object>> ahpCheck(@RequestParam(required = false) Long groupId) {
        Long courseId = groupId != null ? resolveCourseId(groupId) : null;
        var wq = new LambdaQueryWrapper<CourseObjective>().orderByAsc(CourseObjective::getSortOrder);
        if (courseId != null) wq.eq(CourseObjective::getCourseId, courseId);
        List<CourseObjective> objs = objectiveMapper.selectList(wq);
        double sum = objs.stream().mapToDouble(o -> o.getWeight() != null ? o.getWeight() : 0).sum();

        // 尝试加载课程的两两比较矩阵进行真实AHP计算
        boolean hasPairwiseMatrix = false;
        Map<String, Object> ahpResult = null;
        if (courseId != null) {
            Course course = courseMapper.selectById(courseId);
            if (course != null && course.getPairwiseMatrix() != null && !course.getPairwiseMatrix().isBlank()) {
                try {
                    double[][] matrix = objectMapper.readValue(course.getPairwiseMatrix(), double[][].class);
                    if (matrix.length == objs.size()) {
                        ahpResult = ahpService.calculate(matrix);
                        hasPairwiseMatrix = true;
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse pairwise matrix for course {}: {}", courseId, e.getMessage());
                }
            }
        }

        if (hasPairwiseMatrix) {
            // 真实AHP结果
            double cr = ((Number) ahpResult.get("CR")).doubleValue();
            boolean consistent = (boolean) ahpResult.get("consistent");
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("cr", cr);
            result.put("CI", ahpResult.get("CI"));
            result.put("lambdaMax", ahpResult.get("lambdaMax"));
            result.put("ri", ahpResult.get("ri"));
            result.put("totalWeight", Math.round(sum * 100.0) / 100.0);
            result.put("valid", consistent && Math.abs(sum - 1.0) < 0.01);
            result.put("consistent", consistent);
            result.put("hasPairwiseMatrix", true);
            result.put("message", consistent ? "AHP一致性检验通过 (CR=" + cr + " < 0.1)" : "⚠ 一致性检验不通过！CR=" + cr + " ≥ 0.1，请调整两两比较矩阵");
            result.put("weights", ahpResult.get("weights"));
            result.put("objectives", objs.stream().map(o -> Map.of("id", o.getId(), "title", o.getTitle(),
                "dimension", o.getDimension() != null ? o.getDimension() : "",
                "weight", o.getWeight() != null ? o.getWeight() : 0)).toList());
            return R.ok(result);
        }

        // 无两两比较矩阵时的简单检查
        boolean valid = objs.isEmpty() || Math.abs(sum - 1.0) < 0.01;
        Map<String, Object> fallbackResult = new LinkedHashMap<>();
        fallbackResult.put("cr", null);
        fallbackResult.put("lambdaMax", null);
        fallbackResult.put("totalWeight", Math.round(sum * 100.0) / 100.0);
        fallbackResult.put("valid", valid);
        fallbackResult.put("hasPairwiseMatrix", false);
        fallbackResult.put("message", valid ? "权重之和为1.0（简易检查通过）。建议配置两两比较矩阵以获得AHP一致性检验。" : "权重之和为" + String.format("%.2f", sum) + "，建议调整为1.0。同时建议配置两两比较矩阵以进行AHP一致性检验。");
        fallbackResult.put("objectives", objs.stream().map(o -> Map.of("id", o.getId(), "title", o.getTitle(),
            "dimension", o.getDimension() != null ? o.getDimension() : "",
            "weight", o.getWeight() != null ? o.getWeight() : 0)).toList());
        return R.ok(fallbackResult);
    }

    @PostMapping("/ahp-matrix")
    @Operation(summary = "保存课程的两两比较矩阵（用于AHP一致性检验）")
    public R<Map<String, Object>> saveAhpMatrix(@RequestBody Map<String, Object> body) {
        Long courseId = Long.valueOf(body.get("courseId").toString());
        Course course = courseMapper.selectById(courseId);
        if (course == null) return R.fail(404, "课程不存在");
        try {
            String matrixJson = objectMapper.writeValueAsString(body.get("matrix"));
            course.setPairwiseMatrix(matrixJson);
            courseMapper.updateById(course);
            // 立即验证
            @SuppressWarnings("unchecked")
            var matrixRaw = (List<List<Number>>) body.get("matrix");
            int n = matrixRaw.size();
            double[][] matrix = new double[n][n];
            for (int i = 0; i < n; i++) {
                var row = matrixRaw.get(i);
                for (int j = 0; j < n; j++) matrix[i][j] = row.get(j).doubleValue();
            }
            Map<String, Object> result = ahpService.calculate(matrix);
            return R.ok(Map.of("saved", true, "ahpResult", result));
        } catch (Exception e) {
            return R.fail(400, "矩阵格式错误: " + e.getMessage());
        }
    }

    @GetMapping("/trend")
    @Operation(summary = "达成度趋势（多轮计算对比）")
    public R<List<Map<String, Object>>> trend(@RequestParam Long groupId) {
        List<AchievementResult> all = resultMapper.selectList(
            new LambdaQueryWrapper<AchievementResult>()
                .eq(AchievementResult::getGroupId, groupId)
                .orderByAsc(AchievementResult::getCalcRound)
                .orderByAsc(AchievementResult::getCalculatedAt));
        // Group by calcRound
        Map<Integer, List<AchievementResult>> byRound = new LinkedHashMap<>();
        for (AchievementResult r : all) {
            int round = r.getCalcRound() != null ? r.getCalcRound() : 1;
            byRound.computeIfAbsent(round, k -> new ArrayList<>()).add(r);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Integer, List<AchievementResult>> entry : byRound.entrySet()) {
            int round = entry.getKey();
            List<AchievementResult> list = entry.getValue();
            double overall = list.isEmpty() ? 0 : list.stream()
                .mapToDouble(r -> r.getAchievementValue() != null ? r.getAchievementValue() : 0)
                .average().orElse(0);
            LocalDateTime calculatedAt = list.get(0).getCalculatedAt();
            result.add(Map.of("calcRound", round, "calculatedAt",
                calculatedAt != null ? calculatedAt.toString() : "",
                "overallAchievement", Math.round(overall * 10000.0) / 10000.0));
        }
        return R.ok(result);
    }

    @GetMapping("/radar")
    @Operation(summary = "雷达图数据（传groupId自动解析courseId）")
    public R<Map<String, Object>> radar(@RequestParam Long groupId) {
        Long courseId = resolveCourseId(groupId);
        if (courseId == null) return R.fail(400, "小组未关联课程");
        List<CourseObjective> objs = objectiveMapper.selectList(
            new LambdaQueryWrapper<CourseObjective>().eq(CourseObjective::getCourseId, courseId).orderByAsc(CourseObjective::getSortOrder));
        Map<String, Double> ds = new LinkedHashMap<>(Map.of("KNOWLEDGE", 0.0, "ABILITY", 0.0, "QUALITY", 0.0));
        Map<String, Long> dc = new LinkedHashMap<>(Map.of("KNOWLEDGE", 0L, "ABILITY", 0L, "QUALITY", 0L));
        for (CourseObjective obj : objs) {
            var results = resultMapper.selectList(new LambdaQueryWrapper<AchievementResult>()
                .eq(AchievementResult::getObjectiveId, obj.getId()).eq(AchievementResult::getGroupId, groupId));
            double avg = results.isEmpty() ? 0 : results.stream().mapToDouble(r -> r.getAchievementValue() != null ? r.getAchievementValue() : 0).average().orElse(0);
            ds.merge(obj.getDimension(), avg, Double::sum);
            dc.merge(obj.getDimension(), 1L, Long::sum);
        }
        return R.ok(Map.of("dimensions", List.of("知识掌握", "工程实践能力", "综合素养"),
            "values", List.of(dc.get("KNOWLEDGE") > 0 ? Math.round(ds.get("KNOWLEDGE") / dc.get("KNOWLEDGE") * 100.0) / 100.0 : 0,
                dc.get("ABILITY") > 0 ? Math.round(ds.get("ABILITY") / dc.get("ABILITY") * 100.0) / 100.0 : 0,
                dc.get("QUALITY") > 0 ? Math.round(ds.get("QUALITY") / dc.get("QUALITY") * 100.0) / 100.0 : 0)));
    }

    @GetMapping("/validate-weights")
    @Operation(summary = "权重校验（检查权重之和是否为1.0）")
    public R<Map<String, Object>> validateWeights(@RequestParam(required = false) Long groupId) {
        Long courseId = groupId != null ? resolveCourseId(groupId) : null;
        var wq = new LambdaQueryWrapper<CourseObjective>().orderByAsc(CourseObjective::getSortOrder);
        if (courseId != null) wq.eq(CourseObjective::getCourseId, courseId);
        List<CourseObjective> objs = objectiveMapper.selectList(wq);
        double sum = objs.stream().mapToDouble(o -> o.getWeight() != null ? o.getWeight() : 0).sum();
        boolean valid = objs.isEmpty() || Math.abs(sum - 1.0) < 0.01;
        return R.ok(Map.of("totalWeight", Math.round(sum * 100.0) / 100.0, "valid", valid,
            "message", valid ? "权重配置正确（之和=1.0）" : "权重之和为" + String.format("%.2f", sum) + "，建议调整为1.0。如需AHP一致性检验请使用 /ahp-check 端点。",
            "objectives", objs.stream().map(o -> Map.of("id", o.getId(), "title", o.getTitle(),
                "weight", o.getWeight() != null ? o.getWeight() : 0)).toList()));
    }

    @PostMapping("/suggestions/generate")
    @Operation(summary = "生成改进建议（传groupId自动解析courseId）")
    public R<List<ImprovementSuggestion>> generateSuggestions(@RequestParam Long groupId) {
        Long courseId = resolveCourseId(groupId);
        if (courseId == null) return R.fail(400, "小组未关联课程");
        return R.ok(achievementService.generateSuggestions(courseId, groupId));
    }

    @GetMapping("/suggestions")
    @Operation(summary = "改进建议（按小组过滤）")
    public R<List<ImprovementSuggestion>> listSuggestions(@RequestParam(required = false) Long groupId) {
        var wq = new LambdaQueryWrapper<ImprovementSuggestion>().orderByDesc(ImprovementSuggestion::getCreatedAt);
        if (groupId != null) {
            Long courseId = resolveCourseId(groupId);
            if (courseId != null) {
                var objIds = objectiveMapper.selectList(new LambdaQueryWrapper<CourseObjective>().eq(CourseObjective::getCourseId, courseId)).stream().map(CourseObjective::getId).toList();
                if (!objIds.isEmpty()) wq.in(ImprovementSuggestion::getObjectiveId, objIds);
            }
        }
        return R.ok(suggestionMapper.selectList(wq));
    }

    // ========== PO毕业要求达成度 ==========

    @GetMapping("/po-achievement")
    @Operation(summary = "毕业要求达成度计算（CO→PO映射，最小值法）")
    public R<Map<String, Object>> poAchievement(@RequestParam Long groupId) {
        Long courseId = resolveCourseId(groupId);
        if (courseId == null) return R.fail(400, "小组未关联课程");

        List<CourseObjective> objs = objectiveMapper.selectList(
            new LambdaQueryWrapper<CourseObjective>().eq(CourseObjective::getCourseId, courseId));
        Map<Long, Double> objAchievement = new java.util.LinkedHashMap<>();
        for (CourseObjective obj : objs) {
            var results = resultMapper.selectList(new LambdaQueryWrapper<AchievementResult>()
                .eq(AchievementResult::getObjectiveId, obj.getId()).eq(AchievementResult::getGroupId, groupId));
            objAchievement.put(obj.getId(), results.isEmpty() ? 0 : results.stream()
                .mapToDouble(r -> r.getAchievementValue() != null ? r.getAchievementValue() : 0).average().orElse(0));
        }

        List<com.obe.evaluation.course.entity.GraduationRequirement> allGR = grMapper.selectList(
            new LambdaQueryWrapper<com.obe.evaluation.course.entity.GraduationRequirement>().orderByAsc(com.obe.evaluation.course.entity.GraduationRequirement::getSortOrder));
        List<com.obe.evaluation.course.entity.ObjectiveRequirementMapping> allMappings = mappingMapper.selectList(null);

        Map<Long, List<com.obe.evaluation.course.entity.ObjectiveRequirementMapping>> grToMappings = new java.util.LinkedHashMap<>();
        for (com.obe.evaluation.course.entity.ObjectiveRequirementMapping m : allMappings) {
            grToMappings.computeIfAbsent(m.getRequirementId(), k -> new java.util.ArrayList<>()).add(m);
        }

        List<Map<String, Object>> poDetails = new java.util.ArrayList<>();
        double overallPO = 0;
        int poCount = 0;

        for (com.obe.evaluation.course.entity.GraduationRequirement gr : allGR) {
            var mappings = grToMappings.getOrDefault(gr.getId(), java.util.List.of());
            if (mappings.isEmpty()) continue;

            double weightedSum = 0, totalWeight = 0;
            List<Double> mappedAchievements = new java.util.ArrayList<>();
            for (com.obe.evaluation.course.entity.ObjectiveRequirementMapping m : mappings) {
                double ach = objAchievement.getOrDefault(m.getObjectiveId(), 0.0);
                double w = m.getWeight() != null ? m.getWeight() : 1.0 / Math.max(mappings.size(), 1);
                weightedSum += ach * w;
                totalWeight += w;
                mappedAchievements.add(ach);
            }
            double weightedAch = totalWeight > 0 ? weightedSum / totalWeight : 0;
            // P0-1: 最小值法 — 该毕业要求对应的所有课程目标中取最小值
            double minAch = mappedAchievements.isEmpty() ? 0 : mappedAchievements.stream().min(Double::compare).orElse(0.0);

            poDetails.add(Map.of("requirementId", gr.getId(), "requirementNo", gr.getReqNo(),
                "title", gr.getTitle(), "weightedAchievement", Math.round(weightedAch * 10000.0) / 10000.0,
                "minAchievement", Math.round(minAch * 10000.0) / 10000.0,
                "objectiveCount", mappings.size(), "passed", minAch >= 0.6));
            overallPO += minAch;
            poCount++;
        }

        return R.ok(Map.of("overallPOAchievement", poCount > 0 ? Math.round(overallPO / poCount * 10000.0) / 10000.0 : 0,
            "poDetails", poDetails, "hasMappings", !allMappings.isEmpty(),
            "method", "最小值法：毕业要求达成度=Min{对应课程目标达成度}"));
    }

    // ========== AHP ==========

    @PostMapping("/ahp-calculate")
    @Operation(summary = "AHP两两比较矩阵计算权重")
    public R<Map<String, Object>> ahpCalculate(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        var matrixRaw = (List<List<Number>>) body.get("matrix");
        if (matrixRaw == null || matrixRaw.isEmpty()) return R.fail(400, "请提供两两比较矩阵");
        int n = matrixRaw.size();
        double[][] matrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            var row = matrixRaw.get(i);
            for (int j = 0; j < n; j++) matrix[i][j] = row.get(j).doubleValue();
        }
        return R.ok(ahpService.calculate(matrix));
    }

    // ========== 成绩回溯 ==========

    @GetMapping("/drill-down")
    @Operation(summary = "成绩回溯反查：从达成度下钻到原始评价数据")
    public R<Map<String, Object>> drillDown(@RequestParam Long groupId,
                                             @RequestParam Long objectiveId) {
        // 1. 课程目标信息
        var obj = objectiveMapper.selectById(objectiveId);
        if (obj == null) return R.fail(404, "课程目标不存在");

        // 2. 指标点→评价方式 链
        List<Map<String, Object>> indicatorChain = new ArrayList<>();
        var indicators = indicatorMapper.selectList(
            new LambdaQueryWrapper<GraduationIndicator>().eq(GraduationIndicator::getObjectiveId, objectiveId));

        for (var ind : indicators) {
            Map<String, Object> indDetail = new LinkedHashMap<>();
            indDetail.put("indicatorId", ind.getId());
            indDetail.put("indicatorNo", ind.getIndicatorNo());
            indDetail.put("title", ind.getTitle());

            var methods = methodMapper.selectList(
                new LambdaQueryWrapper<EvaluationMethod>().eq(EvaluationMethod::getIndicatorId, ind.getId()));
            List<Map<String, Object>> methodDetails = new ArrayList<>();
            for (var m : methods) {
                Map<String, Object> md = new LinkedHashMap<>();
                md.put("methodId", m.getId()); md.put("methodName", m.getMethodName());
                md.put("dataSource", m.getDataSource()); md.put("weight", m.getWeight());
                md.put("fullScore", m.getFullScore()); md.put("evalType", m.getEvalType());
                // 获取实际得分
                String ds = m.getDataSource() != null ? m.getDataSource().toLowerCase() : "";
                double actualScore = 0;
                try {
                    switch (ds) {
                        case "git":
                            var commits = gitCommitMapper.selectList(
                                new LambdaQueryWrapper<GitCommitLog>().eq(GitCommitLog::getGroupId, groupId));
                            actualScore = commits.isEmpty() ? 0 : Math.min(100, commits.size() * 5.0);
                            break;
                        case "journal":
                            var journals = journalMapper.selectList(
                                new LambdaQueryWrapper<ProjectJournal>().eq(ProjectJournal::getGroupId, groupId));
                            actualScore = journals.isEmpty() ? 0 : Math.min(100, journals.size() * 10.0);
                            break;
                        case "self_test":
                            var tests = selfTestMapper.selectList(null);
                            actualScore = tests.isEmpty() ? 0 : tests.stream().mapToDouble(t ->
                                t.getTotal() != null && t.getTotal() > 0 ? t.getScore() / t.getTotal() * 100 : 0).average().orElse(0);
                            break;
                        default:
                            var evals = groupEvalMapper.selectList(
                                new LambdaQueryWrapper<GroupEvaluation>().eq(GroupEvaluation::getGroupId, groupId));
                            actualScore = evals.isEmpty() ? 0 : evals.stream().mapToDouble(e ->
                                e.getScore() != null ? e.getScore() : 0).average().orElse(0);
                    }
                } catch (Exception e) { log.debug("Skipping method detail: {}", e.getMessage()); }
                md.put("actualScore", Math.round(actualScore * 100.0) / 100.0);
                md.put("achievement", m.getFullScore() != null && m.getFullScore() > 0
                    ? Math.round(actualScore / m.getFullScore() * 10000.0) / 10000.0 : 0);
                methodDetails.add(md);
            }
            indDetail.put("methods", methodDetails);
            indicatorChain.add(indDetail);
        }

        // 3. 达成度结果
        var results = resultMapper.selectList(
            new LambdaQueryWrapper<AchievementResult>()
                .eq(AchievementResult::getObjectiveId, objectiveId)
                .eq(AchievementResult::getGroupId, groupId)
                .orderByDesc(AchievementResult::getCalcRound));
        double currentAchievement = results.isEmpty() ? 0 :
            results.get(0).getAchievementValue() != null ? results.get(0).getAchievementValue() : 0;

        return R.ok(Map.of(
            "objectiveId", objectiveId,
            "objectiveTitle", obj.getTitle(),
            "currentAchievement", Math.round(currentAchievement * 10000.0) / 10000.0,
            "indicatorChain", indicatorChain,
            "calcRounds", results.stream().map(r -> Map.of(
                "round", r.getCalcRound(), "value", r.getAchievementValue(),
                "time", r.getCalculatedAt() != null ? r.getCalculatedAt().toString() : ""
            )).toList()
        ));
    }

    // ========== PDCA 改进工单 ==========

    @GetMapping("/improvement-tasks")
    @Operation(summary = "查询改进工单（按小组过滤）")
    public R<List<Map<String, Object>>> listTasks(@RequestParam(required = false) Long groupId) {
        var wq = new LambdaQueryWrapper<ImprovementTask>().orderByDesc(ImprovementTask::getCreatedAt);
        if (groupId != null) wq.eq(ImprovementTask::getGroupId, groupId);
        List<ImprovementTask> tasks = taskMapper.selectList(wq);
        // enrich with objective title and resolve assignee
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (ImprovementTask t : tasks) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", t.getId());
            item.put("objectiveId", t.getObjectiveId());
            item.put("groupId", t.getGroupId());
            item.put("title", t.getTitle() != null ? t.getTitle() : "");
            item.put("description", t.getDescription() != null ? t.getDescription() : (t.getSuggestion() != null ? t.getSuggestion() : ""));
            item.put("currentAchievement", t.getCurrentAchievement());
            item.put("suggestion", t.getSuggestion());
            item.put("action", t.getAction());
            item.put("priority", t.getPriority() != null ? t.getPriority() : "LOW");
            item.put("assignee", t.getAssignee());
            item.put("assigneeName", t.getAssigneeName() != null ? t.getAssigneeName() : "");
            item.put("status", t.getStatus());
            item.put("dueDate", t.getDueDate());
            item.put("deadline", t.getDueDate()); // frontend compat
            item.put("createdAt", t.getCreatedAt());
            item.put("updatedAt", t.getUpdatedAt());
            // resolve objective title
            if (t.getObjectiveId() != null) {
                var obj = objectiveMapper.selectById(t.getObjectiveId());
                item.put("objectiveTitle", obj != null ? obj.getTitle() : "");
            }
            enriched.add(item);
        }
        return R.ok(enriched);
    }

    @PostMapping("/improvement-tasks/generate")
    @Operation(summary = "自动生成改进工单（达成度<0.6的目标触发预警）")
    public R<Map<String, Object>> generateTasks(@RequestParam Long groupId) {
        Long courseId = resolveCourseId(groupId);
        if (courseId == null) return R.fail(400, "小组未关联课程");
        var objs = objectiveMapper.selectList(
            new LambdaQueryWrapper<CourseObjective>().eq(CourseObjective::getCourseId, courseId));

        // Find group leader for auto-assignment
        String defaultAssignee = "";
        String defaultAssigneeName = "";
        try {
            var members = groupMemberMapper.selectList(
                new LambdaQueryWrapper<com.obe.evaluation.group.entity.GroupMember>()
                    .eq(com.obe.evaluation.group.entity.GroupMember::getGroupId, groupId));
            for (var m : members) {
                if ("LEADER".equals(m.getRoleCode())) {
                    defaultAssignee = String.valueOf(m.getUserId());
                    var u = userMapper.selectById(m.getUserId());
                    defaultAssigneeName = u != null ? u.getRealName() : "";
                    break;
                }
            }
        } catch (Exception e) { log.debug("Failed to resolve group leader: {}", e.getMessage()); }

        int created = 0;
        for (var obj : objs) {
            var results = resultMapper.selectList(new LambdaQueryWrapper<AchievementResult>()
                .eq(AchievementResult::getObjectiveId, obj.getId()).eq(AchievementResult::getGroupId, groupId));
            double ach = results.isEmpty() ? 0 : results.stream().mapToDouble(r -> r.getAchievementValue()!=null?r.getAchievementValue():0).average().orElse(0);
            if (ach < 0.6) {
                var existing = taskMapper.selectList(new LambdaQueryWrapper<ImprovementTask>()
                    .eq(ImprovementTask::getObjectiveId, obj.getId()).eq(ImprovementTask::getGroupId, groupId)
                    .ne(ImprovementTask::getStatus, "DONE"));
                if (!existing.isEmpty()) continue;

                String priority = ach < 0.3 ? "HIGH" : ach < 0.45 ? "MEDIUM" : "LOW";
                ImprovementTask t = new ImprovementTask();
                t.setObjectiveId(obj.getId()); t.setGroupId(groupId);
                t.setTitle("「" + obj.getTitle() + "」达成度改进");
                t.setDescription("课程目标「" + obj.getTitle() + "」当前达成度为 "
                    + String.format("%.1f", ach * 100) + "%，低于60%及格线。需要针对性改进教学环节以提升达成度。");
                t.setCurrentAchievement(Math.round(ach*10000.0)/10000.0);
                t.setSuggestion("[" + priority + "] 课程目标「" + obj.getTitle() + "」达成度不足60%，建议加强相关教学环节");
                t.setPriority(priority);
                t.setAssignee(defaultAssignee);
                t.setAssigneeName(defaultAssigneeName);
                t.setStatus("PENDING");
                t.setDueDate(LocalDateTime.now().plusWeeks(2));
                t.setCreatedAt(LocalDateTime.now());
                taskMapper.insert(t); created++;
            }
        }
        return R.ok(Map.of("created", created, "message", created>0?"已生成"+created+"条改进工单":"所有目标达成度均≥60%，无需生成工单"));
    }

    @PutMapping("/improvement-tasks/{id}")
    @Operation(summary = "更新工单状态/措施")
    public R<ImprovementTask> updateTask(@PathVariable Long id, @RequestBody ImprovementTask t) {
        t.setId(id); t.setUpdatedAt(LocalDateTime.now()); taskMapper.updateById(t); return R.ok(t);
    }

    @DeleteMapping("/improvement-tasks/{id}")
    public R<Void> deleteTask(@PathVariable Long id) { taskMapper.deleteById(id); return R.ok(); }
}
