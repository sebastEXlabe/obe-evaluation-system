package com.obe.evaluation.evaluation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("personal_score")
public class PersonalScore {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId; private Long groupId;
    private Double groupTotalScore; private Double contributionRatio;
    private Double finalScore; private Double bonusTotal;
    private LocalDateTime calculatedAt;
}
