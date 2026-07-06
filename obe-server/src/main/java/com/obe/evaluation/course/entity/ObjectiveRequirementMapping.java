package com.obe.evaluation.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("objective_requirement_mapping")
public class ObjectiveRequirementMapping {
    @TableId(type = IdType.AUTO) private Long id;
    private Long objectiveId;      // 课程目标ID
    private Long requirementId;    // 毕业要求ID
    private Double weight;         // 映射权重(0~1)
    private String supportLevel;   // 支撑程度 H/M/L (高/中/低)
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt;
}
