package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("period_stats")
public class PeriodStats {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String statMonth;
    private Integer actualDays;
    private Integer predictedDays;
    private Integer cycleLength;
    private Integer periodLength;
    private BigDecimal regularityScore;
    private String flowDistribution;
    private String symptomsSummary;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
