package com.obe.evaluation.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("audit_log")
public class AuditLog {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId; private String username; private String action;
    private String target; private String ip;
    private LocalDateTime createdAt;
}
