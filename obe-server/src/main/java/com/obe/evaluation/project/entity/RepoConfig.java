package com.obe.evaluation.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("repo_config")
public class RepoConfig {
    @TableId(type = IdType.AUTO) private Long id;
    private Long groupId; private String platform; private String owner;
    private String repo; private String accessToken; private String branch;
    private Boolean enabled; private LocalDateTime lastSyncedAt;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt;
}
