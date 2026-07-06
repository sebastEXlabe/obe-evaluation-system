package com.obe.evaluation.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("qa_record")
public class QaRecord {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId; private Long knowledgeId;
    private String question; private String answer;
    private String sessionId; private String contentType;
    private Boolean isResolved; private String questionType;
    private LocalDateTime askedAt;
}
