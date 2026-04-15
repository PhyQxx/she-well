package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("period_record")
public class PeriodRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer duration;
    private Integer flowLevel;
    private String symptoms;
    private String notes;
    private Integer isPhysicalDelete;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
