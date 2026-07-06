package com.obe.evaluation.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("self_test_record")
public class SelfTestRecord {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId;
    private Long knowledgeId;
    private String questions;    // JSON: [{question,options,answer,userAnswer,correct}]
    private Double score;
    private Double total;
    private String feedback;     // AI总体反馈
    private LocalDateTime takenAt;
}
