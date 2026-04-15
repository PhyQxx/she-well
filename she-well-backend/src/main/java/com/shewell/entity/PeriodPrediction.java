package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("period_prediction")
public class PeriodPrediction {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDate predictedStartDate;
    private LocalDate predictedEndDate;
    private LocalDate predictedNextDate;
    private LocalDate fertileStartDate;
    private LocalDate fertileEndDate;
    private LocalDate ovulationDate;
    private Integer cycleLength;
    private Integer periodLength;
    private BigDecimal predictionConfidence;
    private LocalDateTime lastCalculated;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
