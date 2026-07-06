package com.obe.evaluation.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("contribution_log")
public class ContributionLog {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId; private Long groupId; private String description;
    private Double bonusScore; private Boolean approved; private Long approvedBy;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt;
}
