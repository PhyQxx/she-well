package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("body_metrics_record")
public class BodyMetricsRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDate recordDate;
    private String metricType;
    private BigDecimal metricValue;
    private String unit;
    private String source;
    private String notes;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
