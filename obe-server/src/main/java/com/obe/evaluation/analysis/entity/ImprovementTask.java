package com.obe.evaluation.analysis.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("improvement_task")
public class ImprovementTask {
    @TableId(type = IdType.AUTO) private Long id;
    private Long objectiveId;       // 课程目标ID
    private Long groupId;           // 小组ID
    private Double currentAchievement; // 当前达成度
    private String suggestion;      // 改进建议
    private String action;          // 改进措施
    private String assignee;        // 负责人
    private String status;          // PENDING/IN_PROGRESS/DONE
    private LocalDateTime dueDate;
    @TableLogic(value = "0", delval = "1") private Integer deleted;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
