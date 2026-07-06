package com.obe.evaluation.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.common.R;
import com.obe.evaluation.course.entity.GraduationRequirement;
import com.obe.evaluation.course.entity.ObjectiveRequirementMapping;
import com.obe.evaluation.course.mapper.GraduationRequirementMapper;
import com.obe.evaluation.course.mapper.ObjectiveRequirementMappingMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/graduation-requirements") @RequiredArgsConstructor
@Tag(name = "毕业要求与映射")
public class GraduationRequirementController {
    private final GraduationRequirementMapper grMapper;
    private final ObjectiveRequirementMappingMapper mappingMapper;

    @GetMapping
    @Operation(summary = "查询所有毕业要求（工程教育认证12条标准）")
    public R<List<GraduationRequirement>> list() {
        return R.ok(grMapper.selectList(new LambdaQueryWrapper<GraduationRequirement>().orderByAsc(GraduationRequirement::getSortOrder)));
    }

    @PostMapping
    public R<GraduationRequirement> create(@RequestBody GraduationRequirement gr) { grMapper.insert(gr); return R.ok(gr); }

    @PutMapping("/{id}")
    public R<GraduationRequirement> update(@PathVariable Long id, @RequestBody GraduationRequirement gr) { gr.setId(id); grMapper.updateById(gr); return R.ok(gr); }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) { grMapper.deleteById(id); return R.ok(); }

    // ========== 映射管理 ==========

    @GetMapping("/mappings")
    @Operation(summary = "查询课程目标→毕业要求映射")
    public R<List<ObjectiveRequirementMapping>> listMappings(@RequestParam(required = false) Long objectiveId) {
        var wq = new LambdaQueryWrapper<ObjectiveRequirementMapping>();
        if (objectiveId != null) wq.eq(ObjectiveRequirementMapping::getObjectiveId, objectiveId);
        return R.ok(mappingMapper.selectList(wq));
    }

    @PostMapping("/mappings")
    @Operation(summary = "创建课程目标→毕业要求映射")
    public R<ObjectiveRequirementMapping> addMapping(@RequestBody ObjectiveRequirementMapping m) { mappingMapper.insert(m); return R.ok(m); }

    @PutMapping("/mappings/{id}")
    public R<ObjectiveRequirementMapping> updateMapping(@PathVariable Long id, @RequestBody ObjectiveRequirementMapping m) { m.setId(id); mappingMapper.updateById(m); return R.ok(m); }

    @DeleteMapping("/mappings/{id}")
    public R<Void> deleteMapping(@PathVariable Long id) { mappingMapper.deleteById(id); return R.ok(); }
}
