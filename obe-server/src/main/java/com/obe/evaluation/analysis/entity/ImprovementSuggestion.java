package com.obe.evaluation.analysis.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("improvement_suggestion")
public class ImprovementSuggestion {
    @TableId(type = IdType.AUTO) private Long id;
    private Long objectiveId; private Long indicatorId;
    private String suggestion; private Boolean isAuto;
    private LocalDateTime createdAt;
}
