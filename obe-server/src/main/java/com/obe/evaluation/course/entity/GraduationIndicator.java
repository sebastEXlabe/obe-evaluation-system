package com.obe.evaluation.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("graduation_indicator")
public class GraduationIndicator {
    @TableId(type = IdType.AUTO) private Long id;
    private Long objectiveId; private String indicatorNo; private String title;
    private String description; private Integer sortOrder;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
