package com.obe.evaluation.group.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("position_history")
public class PositionHistory {
    @TableId(type = IdType.AUTO) private Long id;
    private Long groupId;
    private Long userId;
    private String oldRole;
    private String newRole;
    private Long changedBy;
    private String reason;
    private LocalDateTime createdAt;
}
