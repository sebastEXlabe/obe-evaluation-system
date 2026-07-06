package com.obe.evaluation.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("course_objective")
public class CourseObjective {
    @TableId(type = IdType.AUTO) private Long id;
    private String objectiveNo; private String title; private String description;
    private String dimension; private Double weight; private Long courseId; private Integer sortOrder;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
