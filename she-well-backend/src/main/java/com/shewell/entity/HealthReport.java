package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("health_report")
public class HealthReport {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String reportType;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal avgCycleLength;
    private BigDecimal avgPeriodLength;
    private BigDecimal regularityScore;
    private String symptomSummary;
    private String advice;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createTime;
}
