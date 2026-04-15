package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("bbt_temp_record")
public class BbtTempRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDate measureDate;
    private BigDecimal temperature;
    private String measurementTime;
    private String measurementMethod;
    private Integer sleepQuality;
    private String notes;
    private LocalDateTime createTime;
}
