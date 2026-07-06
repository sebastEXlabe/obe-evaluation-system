package com.obe.evaluation.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("requirement_change")
public class RequirementChange {
    @TableId(type = IdType.AUTO) private Long id;
    private Long groupId;
    private String title;
    private String description;
    private String changeType;    // ADD / MODIFY / REMOVE
    private String reason;
    private Long createdBy;
    private LocalDateTime createdAt;
}
