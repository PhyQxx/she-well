package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("ovulation_record")
public class OvulationRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDate recordDate;
    private Integer ovulationStatus;
    private String ovulationWay;
    private Integer confidence;
    private BigDecimal temperature;
    private Integer lhSurge;
    private BigDecimal follicularSize;
    private String symptoms;
    private String notes;
    private LocalDateTime createTime;
}
