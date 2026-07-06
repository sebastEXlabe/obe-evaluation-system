package com.obe.evaluation.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.common.R;
import com.obe.evaluation.course.entity.CdioPhaseMapping;
import com.obe.evaluation.course.entity.CourseObjective;
import com.obe.evaluation.course.entity.EvaluationMethod;
import com.obe.evaluation.course.entity.GraduationIndicator;
import com.obe.evaluation.course.mapper.CdioPhaseMappingMapper;
import com.obe.evaluation.course.mapper.CourseObjectiveMapper;
import com.obe.evaluation.course.mapper.EvaluationMethodMapper;
import com.obe.evaluation.course.mapper.GraduationIndicatorMapper;
import com.obe.evaluation.system.entity.Course;
import com.obe.evaluation.system.entity.SysUser;
import com.obe.evaluation.system.mapper.CourseMapper;
import com.obe.evaluation.system.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/courses") @RequiredArgsConstructor
@Tag(name = "课程管理")
public class CourseManageController {
    private final CourseMapper courseMapper;
    private final SysUserMapper userMapper;
    private final CourseObjectiveMapper courseObjectiveMapper;
    private final GraduationIndicatorMapper graduationIndicatorMapper;
    private final EvaluationMethodMapper evaluationMethodMapper;
    private final CdioPhaseMappingMapper cdioPhaseMappingMapper;

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

    @GetMapping
    public R<List<Map<String, Object>>> list() {
        List<Course> courses;
        if (isAdmin()) courses = courseMapper.selectList(null);
        else courses = courseMapper.selectList(new LambdaQueryWrapper<Course>().eq(Course::getTeacherId, currentUserId()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Course c : courses) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", c.getId()); item.put("courseName", c.getCourseName());
            item.put("semester", c.getSemester()); item.put("teacherId", c.getTeacherId());
            item.put("createdAt", c.getCreatedAt());
            var teacher = userMapper.selectById(c.getTeacherId());
            item.put("teacherName", teacher != null ? teacher.getRealName() : "");
            result.add(item);
        }
        return R.ok(result);
    }

    @PostMapping
    public R<Course> create(@RequestBody Course c) {
        if (c.getCourseName() == null || c.getCourseName().isBlank()) return R.fail(400, "课程名称不能为空");
        if (!isAdmin()) c.setTeacherId(currentUserId());
        courseMapper.insert(c); return R.ok(c);
    }

    @PutMapping
    public R<Course> update(@RequestBody Course c) {
        if (!isAdmin()) {
            Course existing = courseMapper.selectById(c.getId());
            if (existing == null || !existing.getTeacherId().equals(currentUserId()))
                return R.fail(403, "无权修改此课程");
            c.setTeacherId(existing.getTeacherId());
        }
        courseMapper.updateById(c); return R.ok(c);
    }

    private boolean isTeacher() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        if (!isAdmin()) {
            Course existing = courseMapper.selectById(id);
            if (existing == null || !existing.getTeacherId().equals(currentUserId()))
                return R.fail(403, "无权删除此课程");
        }
        courseMapper.deleteById(id);
        return R.ok();
    }

    @PostMapping("/{id}/clone")
    @Operation(summary = "克隆课程（管理员/教师）")
    @Transactional
    public R<Course> cloneCourse(@PathVariable Long id) {
        if (!isAdmin() && !isTeacher()) return R.fail(403, "无权限");
        Course original = courseMapper.selectById(id);
        if (original == null) return R.fail(404, "课程不存在");

        // FIX 6: Deep copy the course with all related data
        // 1. Create the new course
        Course newCourse = new Course();
        newCourse.setCourseName(original.getCourseName() + " - 副本");
        newCourse.setSemester(original.getSemester());
        newCourse.setTeacherId(original.getTeacherId());
        courseMapper.insert(newCourse);
        Long newCourseId = newCourse.getId();

        // 2. Copy course objectives (track old->new id mapping)
        Map<Long, Long> objectiveIdMap = new HashMap<>();
        List<CourseObjective> objectives = courseObjectiveMapper.selectList(
                new LambdaQueryWrapper<CourseObjective>().eq(CourseObjective::getCourseId, id));
        for (CourseObjective obj : objectives) {
            Long oldObjId = obj.getId();
            obj.setId(null);
            obj.setCourseId(newCourseId);
            courseObjectiveMapper.insert(obj);
            objectiveIdMap.put(oldObjId, obj.getId());
        }

        // 3. Copy graduation indicators (using the objective id map)
        for (Long oldObjId : objectiveIdMap.keySet()) {
            Long newObjId = objectiveIdMap.get(oldObjId);
            Map<Long, Long> indicatorIdMap = new HashMap<>();
            List<GraduationIndicator> indicators = graduationIndicatorMapper.selectList(
                    new LambdaQueryWrapper<GraduationIndicator>().eq(GraduationIndicator::getObjectiveId, oldObjId));
            for (GraduationIndicator ind : indicators) {
                Long oldIndId = ind.getId();
                ind.setId(null);
                ind.setObjectiveId(newObjId);
                graduationIndicatorMapper.insert(ind);
                indicatorIdMap.put(oldIndId, ind.getId());

                // 4. Copy evaluation methods
                List<EvaluationMethod> methods = evaluationMethodMapper.selectList(
                        new LambdaQueryWrapper<EvaluationMethod>().eq(EvaluationMethod::getIndicatorId, oldIndId));
                for (EvaluationMethod m : methods) {
                    m.setId(null);
                    m.setIndicatorId(ind.getId());
                    evaluationMethodMapper.insert(m);
                }
            }

            // 5. Copy CDIO phase mappings for this course + each old indicator
            for (Long oldIndId : indicatorIdMap.keySet()) {
                Long newIndId = indicatorIdMap.get(oldIndId);
                List<CdioPhaseMapping> cdioList = cdioPhaseMappingMapper.selectList(
                        new LambdaQueryWrapper<CdioPhaseMapping>()
                                .eq(CdioPhaseMapping::getCourseId, id)
                                .eq(CdioPhaseMapping::getIndicatorId, oldIndId));
                for (CdioPhaseMapping cdio : cdioList) {
                    cdio.setId(null);
                    cdio.setCourseId(newCourseId);
                    cdio.setIndicatorId(newIndId);
                    cdioPhaseMappingMapper.insert(cdio);
                }
            }

            // Also copy CDIO mappings that reference the old course but old indicators directly
            // (those captured by the outer loop, so we don't need an extra pass)
        }

        // Copy any remaining CDIO mappings that reference the original course
        // (some CDIO mappings might reference indicators not belonging to objectives of this course)
        List<CdioPhaseMapping> remainingCdio = cdioPhaseMappingMapper.selectList(
                new LambdaQueryWrapper<CdioPhaseMapping>().eq(CdioPhaseMapping::getCourseId, id));
        for (CdioPhaseMapping cdio : remainingCdio) {
            boolean alreadyCopied = objectiveIdMap.values().stream().anyMatch(
                    v -> cdioPhaseMappingMapper.selectCount(
                            new LambdaQueryWrapper<CdioPhaseMapping>()
                                    .eq(CdioPhaseMapping::getCourseId, newCourseId)
                                    .eq(CdioPhaseMapping::getIndicatorId, cdio.getIndicatorId())
                                    .eq(CdioPhaseMapping::getCdioPhase, cdio.getCdioPhase())) > 0);
            if (alreadyCopied) continue;
            cdio.setId(null);
            cdio.setCourseId(newCourseId);
            cdioPhaseMappingMapper.insert(cdio);
        }

        return R.ok(newCourse);
    }
}
