package com.obe.evaluation.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("quiz")
public class Quiz {
    @TableId(type = IdType.AUTO) private Long id;
    private Long teacherId;        // 出题教师
    private Long groupId;          // 指定小组（null=全部）
    private Long courseId;         // 关联课程
    private String title;          // 测验标题
    private String questions;      // JSON：题目列表
    private String status;         // DRAFT / PUBLISHED / CLOSED
    private Integer totalScore;    // 总分
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
}
