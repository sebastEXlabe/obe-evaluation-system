package com.obe.evaluation.analysis.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("achievement_result")
public class AchievementResult {
    @TableId(type = IdType.AUTO) private Long id;
    private Long objectiveId; private Long groupId;
    private Double achievementValue; private String dimension;
    private Integer calcRound;
    private LocalDateTime calculatedAt;
}
