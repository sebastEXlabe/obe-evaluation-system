package com.obe.evaluation.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @TableName("project_milestone")
public class ProjectMilestone {
    @TableId(type = IdType.AUTO) private Long id;
    private Long groupId; private String title; private String cdioPhase;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private Boolean isDone;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime finishedAt; private LocalDateTime createdAt;
}
