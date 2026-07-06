package com.obe.evaluation.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.obe.evaluation.common.PageQuery;
import com.obe.evaluation.common.R;
import com.obe.evaluation.course.entity.*;
import com.obe.evaluation.course.mapper.*;
import com.obe.evaluation.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController @RequestMapping("/api/course") @RequiredArgsConstructor
@Tag(name = "课程目标与指标")
public class CourseController {
    private final CourseService courseService;
    private final CourseObjectiveMapper objectiveMapper;
    private final GraduationIndicatorMapper indicatorMapper;
    private final EvaluationMethodMapper evalMethodMapper;
    private final CdioPhaseMappingMapper cdioMapper;

    @GetMapping("/tree")
    public R<?> tree(@RequestParam(required = false) Long courseId) { return R.ok(courseService.getFullTree(courseId)); }

    @GetMapping("/objectives")
    public R<Page<CourseObjective>> listObjectives(@RequestParam(required = false) Long courseId, PageQuery q) {
        var wq = new LambdaQueryWrapper<CourseObjective>().orderByAsc(CourseObjective::getSortOrder);
        if (courseId != null) wq.eq(CourseObjective::getCourseId, courseId);
        return R.ok(objectiveMapper.selectPage(new Page<>(q.getPage(), q.getSize()), wq));
    }
    @PostMapping("/objectives") public R<CourseObjective> add(@RequestBody CourseObjective obj) {
        objectiveMapper.insert(obj);
        validateWeights(obj.getCourseId());
        return R.ok(obj);
    }
    @PutMapping("/objectives") public R<CourseObjective> update(@RequestBody CourseObjective obj) {
        objectiveMapper.updateById(obj);
        validateWeights(obj.getCourseId());
        return R.ok(obj);
    }

    private void validateWeights(Long courseId) {
        if (courseId == null) return;
        List<CourseObjective> objectives = objectiveMapper.selectList(
                new LambdaQueryWrapper<CourseObjective>().eq(CourseObjective::getCourseId, courseId));
        double totalWeight = 0;
        for (CourseObjective obj : objectives) {
            if (obj.getWeight() != null) totalWeight += obj.getWeight();
        }
        if (totalWeight > 1.0) {
            log.warn("Course {} objectives total weight ({}) exceeds 1.0", courseId, totalWeight);
        }
    }
    @DeleteMapping("/objectives/{id}") public R<Void> delObj(@PathVariable Long id) { courseService.deleteObjectiveCascade(id); return R.ok(); }

    @PostMapping("/objectives/import")
    @Operation(summary = "批量导入课程目标")
    public R<Map<String, Object>> importObjectives(@RequestBody List<CourseObjective> objectives) {
        int success = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < objectives.size(); i++) {
            CourseObjective obj = objectives.get(i);
            try {
                if (obj.getObjectiveNo() == null || obj.getObjectiveNo().isBlank()) {
                    failed++;
                    errors.add("第" + (i + 1) + "行: objectiveNo不能为空");
                    continue;
                }
                if (obj.getTitle() == null || obj.getTitle().isBlank()) {
                    failed++;
                    errors.add("第" + (i + 1) + "行: title不能为空");
                    continue;
                }
                if (obj.getCourseId() == null) {
                    failed++;
                    errors.add("第" + (i + 1) + "行: courseId不能为空");
                    continue;
                }
                objectiveMapper.insert(obj);
                success++;
                validateWeights(obj.getCourseId());
            } catch (Exception e) {
                log.error("导入课程目标失败 行{}: {}", i + 1, e.getMessage());
                failed++;
                errors.add("第" + (i + 1) + "行: " + e.getMessage());
            }
        }
        return R.ok(Map.of("success", success, "failed", failed, "errors", errors));
    }

    @GetMapping("/indicators")
    public R<Page<GraduationIndicator>> listIndicators(@RequestParam(required = false) Long objectiveId, PageQuery q) {
        var wq = new LambdaQueryWrapper<GraduationIndicator>().orderByAsc(GraduationIndicator::getSortOrder);
        if (objectiveId != null) wq.eq(GraduationIndicator::getObjectiveId, objectiveId);
        return R.ok(indicatorMapper.selectPage(new Page<>(q.getPage(), q.getSize()), wq));
    }
    @PostMapping("/indicators") public R<GraduationIndicator> add(@RequestBody GraduationIndicator ind) { indicatorMapper.insert(ind); return R.ok(ind); }
    @PutMapping("/indicators") public R<GraduationIndicator> update(@RequestBody GraduationIndicator ind) { indicatorMapper.updateById(ind); return R.ok(ind); }
    @DeleteMapping("/indicators/{id}") public R<Void> delInd(@PathVariable Long id) { courseService.deleteIndicatorCascade(id); return R.ok(); }

    @GetMapping("/methods")
    public R<Page<EvaluationMethod>> listMethods(@RequestParam(required = false) Long indicatorId, PageQuery q) {
        var wq = new LambdaQueryWrapper<EvaluationMethod>();
        if (indicatorId != null) wq.eq(EvaluationMethod::getIndicatorId, indicatorId);
        return R.ok(evalMethodMapper.selectPage(new Page<>(q.getPage(), q.getSize()), wq));
    }
    @PostMapping("/methods") public R<EvaluationMethod> add(@RequestBody EvaluationMethod m) { evalMethodMapper.insert(m); return R.ok(m); }
    @PutMapping("/methods") public R<EvaluationMethod> update(@RequestBody EvaluationMethod m) { evalMethodMapper.updateById(m); return R.ok(m); }
    @DeleteMapping("/methods/{id}") public R<Void> delMethod(@PathVariable Long id) { evalMethodMapper.deleteById(id); return R.ok(); }

    @GetMapping("/cdio-mappings")
    public R<Page<CdioPhaseMapping>> listCdio(@RequestParam(required = false) Long indicatorId, PageQuery q) {
        var wq = new LambdaQueryWrapper<CdioPhaseMapping>();
        if (indicatorId != null) wq.eq(CdioPhaseMapping::getIndicatorId, indicatorId);
        return R.ok(cdioMapper.selectPage(new Page<>(q.getPage(), q.getSize()), wq));
    }
    @PostMapping("/cdio-mappings") public R<CdioPhaseMapping> add(@RequestBody CdioPhaseMapping m) { cdioMapper.insert(m); return R.ok(m); }
    @PutMapping("/cdio-mappings") public R<CdioPhaseMapping> update(@RequestBody CdioPhaseMapping m) { cdioMapper.updateById(m); return R.ok(m); }
    @DeleteMapping("/cdio-mappings/{id}") public R<Void> delCdio(@PathVariable Long id) { cdioMapper.deleteById(id); return R.ok(); }
}
