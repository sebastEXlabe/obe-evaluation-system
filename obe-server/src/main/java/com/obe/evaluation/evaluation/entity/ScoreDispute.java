package com.obe.evaluation.evaluation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("score_dispute")
public class ScoreDispute {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId;
    private Long scoreId;
    private String scoreType;
    private String reason;
    private String status;
    private Long resolvedBy;
    private String resolution;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
