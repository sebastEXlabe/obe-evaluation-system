package com.obe.evaluation.group.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("group_member")
public class GroupMember {
    @TableId(type = IdType.AUTO) private Long id;
    private Long groupId; private Long userId; private String roleCode;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime joinAt;
}
