package com.obe.evaluation.group.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("project_group")
public class ProjectGroup {
    @TableId(type = IdType.AUTO) private Long id;
    private String groupName; private Long teacherId; private Long courseId;
    private String inviteCode; private Integer maxMembers; private Integer status;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
