package com.obe.evaluation.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("course")
public class Course {
    @TableId(type = IdType.AUTO) private Long id;
    private String courseName; private String semester; private Long teacherId;
    /** AHP pairwise comparison matrix stored as JSON (n×n double[][]) */
    private String pairwiseMatrix;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt;
}
