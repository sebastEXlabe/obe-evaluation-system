package com.obe.evaluation.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("project_task")
public class ProjectTask {
    @TableId(type = IdType.AUTO) private Long id;
    private Long groupId; private Long milestoneId; private Long assigneeId;
    private String title; private String status; private Integer priority; private String dueDate;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
