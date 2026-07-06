package com.obe.evaluation.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("cdio_phase_mapping")
public class CdioPhaseMapping {
    @TableId(type = IdType.AUTO) private Long id;
    private Long indicatorId; private Long courseId;
    private String cdioPhase; private Integer sortOrder;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt;
}
