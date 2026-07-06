package com.obe.evaluation.analysis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.analysis.entity.AchievementResult;
import com.obe.evaluation.analysis.mapper.AchievementResultMapper;
import com.obe.evaluation.common.R;
import com.obe.evaluation.course.entity.CourseObjective;
import com.obe.evaluation.course.entity.GraduationRequirement;
import com.obe.evaluation.course.mapper.CourseObjectiveMapper;
import com.obe.evaluation.course.mapper.GraduationRequirementMapper;
import com.obe.evaluation.course.mapper.ObjectiveRequirementMappingMapper;
import com.obe.evaluation.evaluation.entity.PersonalScore;
import com.obe.evaluation.evaluation.mapper.PersonalScoreMapper;
import com.obe.evaluation.group.mapper.ProjectGroupMapper;
import com.obe.evaluation.project.mapper.GitCommitLogMapper;
import com.obe.evaluation.project.mapper.ProjectJournalMapper;
import com.obe.evaluation.qa.mapper.QaRecordMapper;
import com.obe.evaluation.qa.mapper.SelfTestRecordMapper;
import com.obe.evaluation.system.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController @RequestMapping("/api/export") @RequiredArgsConstructor
@Tag(name = "数据导出")
public class ExportController {
    private final AchievementResultMapper resultMapper;
    private final CourseObjectiveMapper objectiveMapper;
    private final GraduationRequirementMapper grMapper;
    private final ObjectiveRequirementMappingMapper mappingMapper;
    private final PersonalScoreMapper scoreMapper;
    private final SysUserMapper userMapper;
    private final ProjectGroupMapper groupMapper;
    private final GitCommitLogMapper gitMapper;
    private final ProjectJournalMapper journalMapper;
    private final QaRecordMapper qaMapper;
    private final SelfTestRecordMapper testMapper;

    private Long resolveCourseId(Long groupId) {
        if (groupId == null) return null;
        var g = groupMapper.selectById(groupId);
        return g != null ? g.getCourseId() : null;
    }

    @GetMapping("/achievement-csv")
    @Operation(summary = "导出达成度报告 CSV")
    public void exportAchievementCSV(@RequestParam Long groupId, HttpServletResponse response) throws IOException {
        Long courseId = resolveCourseId(groupId);
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=achievement_group"+groupId+".csv");
        response.setCharacterEncoding("UTF-8");
        PrintWriter w = response.getWriter();
        w.write('﻿'); // BOM for Excel UTF-8
        w.println("课程目标,维度,权重,达成度(加权平均),状态");

        var objs = objectiveMapper.selectList(
            new LambdaQueryWrapper<CourseObjective>().eq(courseId != null, CourseObjective::getCourseId, courseId));
        for (var obj : objs) {
            var results = resultMapper.selectList(new LambdaQueryWrapper<AchievementResult>()
                .eq(AchievementResult::getObjectiveId, obj.getId()).eq(AchievementResult::getGroupId, groupId));
            double ach = results.isEmpty() ? 0 : results.stream().mapToDouble(r -> r.getAchievementValue() != null ? r.getAchievementValue() : 0).average().orElse(0);
            w.printf("%s,%s,%.0f%%,%.1f%%,%s\n",
                obj.getTitle(), obj.getDimension(), (obj.getWeight()!=null?obj.getWeight():0)*100,
                ach*100, ach>=0.6?"已达成":"未达成");
        }

        // PO section
        w.println();
        w.println("毕业要求,最小值达成度,加权达成度,状态");
        List<GraduationRequirement> allGR = grMapper.selectList(null);
        var allMappings = mappingMapper.selectList(null);
        Map<Long, Double> objAch = new HashMap<>();
        for (var obj : objs) {
            var results = resultMapper.selectList(new LambdaQueryWrapper<AchievementResult>()
                .eq(AchievementResult::getObjectiveId, obj.getId()).eq(AchievementResult::getGroupId, groupId));
            objAch.put(obj.getId(), results.isEmpty()?0:results.stream().mapToDouble(r->r.getAchievementValue()!=null?r.getAchievementValue():0).average().orElse(0));
        }
        for (var gr : allGR) {
            var maps = allMappings.stream().filter(m->m.getRequirementId().equals(gr.getId())).toList();
            if (maps.isEmpty()) continue;
            double minAch = maps.stream().mapToDouble(m->objAch.getOrDefault(m.getObjectiveId(),0.0)).min().orElse(0);
            double mappedCount = maps.size();
            double wavg = maps.stream().mapToDouble(m->{
                double a=objAch.getOrDefault(m.getObjectiveId(),0.0);
                double mw=m.getWeight()!=null?m.getWeight():1.0/mappedCount;
                return a*mw;
            }).sum() / maps.stream().mapToDouble(m->m.getWeight()!=null?m.getWeight():1.0/mappedCount).sum();
            w.printf("%s %s,%.1f%%,%.1f%%,%s\n", gr.getReqNo(), gr.getTitle(), minAch*100, wavg*100, minAch>=0.6?"已达成":"未达成");
        }

        // Scores
        w.println();
        w.println("学生姓名,小组得分,贡献系数,加分,最终成绩");
        var scores = scoreMapper.selectList(new LambdaQueryWrapper<PersonalScore>().eq(PersonalScore::getGroupId, groupId));
        for (var s : scores) {
            var u = userMapper.selectById(s.getUserId());
            w.printf("%s,%.1f,%.2f,%.1f,%.1f\n",
                u!=null?u.getRealName():"用户"+s.getUserId(),
                s.getGroupTotalScore()!=null?s.getGroupTotalScore():0,
                s.getContributionRatio()!=null?s.getContributionRatio():0,
                s.getBonusTotal()!=null?s.getBonusTotal():0,
                s.getFinalScore()!=null?s.getFinalScore():0);
        }

        // Summary stats
        w.println();
        w.printf("导出时间,%s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        w.printf("Git提交总数,%d\n", gitMapper.selectCount(new LambdaQueryWrapper<>()));
        w.printf("日志总数,%d\n", journalMapper.selectCount(new LambdaQueryWrapper<>()));
        w.printf("问答总数,%d\n", qaMapper.selectCount(new LambdaQueryWrapper<>()));
        w.flush();
    }
}
