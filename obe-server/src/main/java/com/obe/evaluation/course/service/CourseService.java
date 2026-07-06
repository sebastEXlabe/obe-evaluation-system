package com.obe.evaluation.course.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.course.entity.*;
import com.obe.evaluation.course.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor
public class CourseService {
    private final CourseObjectiveMapper objectiveMapper;
    private final GraduationIndicatorMapper indicatorMapper;
    private final EvaluationMethodMapper evalMethodMapper;
    private final CdioPhaseMappingMapper cdioMapper;

    public List<Map<String, Object>> getFullTree(Long courseId) {
        var wq = new LambdaQueryWrapper<CourseObjective>().orderByAsc(CourseObjective::getSortOrder);
        if (courseId != null) wq.eq(CourseObjective::getCourseId, courseId);
        List<CourseObjective> objectives = objectiveMapper.selectList(wq);
        List<Map<String, Object>> tree = new ArrayList<>();
        for (CourseObjective obj : objectives) {
            Map<String, Object> objNode = new LinkedHashMap<>();
            objNode.put("objective", obj);
            List<GraduationIndicator> indicators = indicatorMapper.selectList(
                new LambdaQueryWrapper<GraduationIndicator>().eq(GraduationIndicator::getObjectiveId, obj.getId()).orderByAsc(GraduationIndicator::getSortOrder));
            List<Map<String, Object>> indList = new ArrayList<>();
            for (GraduationIndicator ind : indicators) {
                Map<String, Object> indNode = new LinkedHashMap<>();
                indNode.put("indicator", ind);
                indNode.put("methods", evalMethodMapper.selectList(
                    new LambdaQueryWrapper<EvaluationMethod>().eq(EvaluationMethod::getIndicatorId, ind.getId())));
                indNode.put("cdioPhases", cdioMapper.selectList(
                    new LambdaQueryWrapper<CdioPhaseMapping>().eq(CdioPhaseMapping::getIndicatorId, ind.getId())));
                indList.add(indNode);
            }
            objNode.put("indicators", indList); tree.add(objNode);
        }
        return tree;
    }

    @Transactional
    public void deleteObjectiveCascade(Long id) {
        var indicators = indicatorMapper.selectList(new LambdaQueryWrapper<GraduationIndicator>().eq(GraduationIndicator::getObjectiveId, id));
        for (var ind : indicators) {
            evalMethodMapper.delete(new LambdaQueryWrapper<EvaluationMethod>().eq(EvaluationMethod::getIndicatorId, ind.getId()));
            cdioMapper.delete(new LambdaQueryWrapper<CdioPhaseMapping>().eq(CdioPhaseMapping::getIndicatorId, ind.getId()));
            indicatorMapper.deleteById(ind.getId());
        }
        objectiveMapper.deleteById(id);
    }

    @Transactional
    public void deleteIndicatorCascade(Long id) {
        evalMethodMapper.delete(new LambdaQueryWrapper<EvaluationMethod>().eq(EvaluationMethod::getIndicatorId, id));
        cdioMapper.delete(new LambdaQueryWrapper<CdioPhaseMapping>().eq(CdioPhaseMapping::getIndicatorId, id));
        indicatorMapper.deleteById(id);
    }
}
