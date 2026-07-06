package com.obe.evaluation.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("knowledge_point")
public class KnowledgePoint {
    @TableId(type = IdType.AUTO) private Long id;
    private String title; private String chapter; private String content;
    private String attachments;  // JSON: [{name,path,size,contentType}]
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt;
}
