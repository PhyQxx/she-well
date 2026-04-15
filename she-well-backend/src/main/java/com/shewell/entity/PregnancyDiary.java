package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("pregnancy_diary")
public class PregnancyDiary {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long pregnancyId;
    private LocalDate recordDate;
    private BigDecimal weight;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private BigDecimal bloodSugar;
    private Integer fetalMovementCount;
    private String mood;
    private String symptoms;
    private String notes;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
