package com.obe.evaluation.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("question_answer")
public class QuestionAnswer {
    @TableId(type = IdType.AUTO) private Long id;
    private Long groupId;         // 所属小组，null表示跨组
    private Long askUserId;       // 提问学生
    private Long answerUserId;    // 回答教师
    private String title;         // 问题标题
    private String question;      // 问题内容
    private String answer;        // 教师回答
    private String status;        // PENDING / ANSWERED / RESOLVED
    private String attachments;   // JSON: [{name,path,size,contentType}]
    private LocalDateTime askedAt;
    private LocalDateTime answeredAt;
    private LocalDateTime resolvedAt;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt;
}
