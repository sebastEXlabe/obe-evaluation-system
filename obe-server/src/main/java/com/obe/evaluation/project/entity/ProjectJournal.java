package com.obe.evaluation.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @TableName("project_journal")
public class ProjectJournal {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId; private Long groupId; private String content;
    private LocalDate journalDate;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt;
}
