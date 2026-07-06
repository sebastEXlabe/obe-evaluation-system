package com.obe.evaluation.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("git_commit_log")
public class GitCommitLog {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId; private Long groupId; private String repoName;
    private String commitHash; private String message;
    private Integer additions; private Integer deletions;
    private LocalDateTime committedAt; private LocalDateTime syncedAt;
}
