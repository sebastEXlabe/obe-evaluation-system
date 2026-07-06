package com.obe.evaluation.evaluation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("group_evaluation")
public class GroupEvaluation {
    @TableId(type = IdType.AUTO) private Long id;
    private Long groupId; private Long evaluatorId;
    private String dimension; private Double score; private String comment;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt;
}
