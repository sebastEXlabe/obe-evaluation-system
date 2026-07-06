package com.obe.evaluation.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("evaluation_method")
public class EvaluationMethod {
    @TableId(type = IdType.AUTO) private Long id;
    private Long indicatorId; private String methodName;
    private Double weight; private String dataSource;
    private String evalType;
    private Double fullScore; private String remark;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
