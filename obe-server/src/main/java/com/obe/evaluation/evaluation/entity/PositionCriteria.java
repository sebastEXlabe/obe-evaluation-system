package com.obe.evaluation.evaluation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data @TableName("position_criteria")
public class PositionCriteria {
    @TableId(type = IdType.AUTO) private Long id;
    private String roleCode;
    private String dimension;
    private Double weight;
    private String description;
}
