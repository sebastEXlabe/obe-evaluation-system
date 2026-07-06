package com.obe.evaluation.evaluation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("role_evaluation")
public class RoleEvaluation {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId; private Long groupId; private Long evaluatorId;
    private String roleCode; private String dimension;
    private Double score; private String comment;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt;
}
