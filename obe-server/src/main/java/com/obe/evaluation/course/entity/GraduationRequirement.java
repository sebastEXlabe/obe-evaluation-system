package com.obe.evaluation.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("graduation_requirement")
public class GraduationRequirement {
    @TableId(type = IdType.AUTO) private Long id;
    private String reqNo;       // 编号如 GR-01
    private String title;       // 毕业要求标题
    private String description; // 详细描述
    private Integer sortOrder;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
