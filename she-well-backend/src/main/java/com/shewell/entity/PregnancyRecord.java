package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("pregnancy_record")
public class PregnancyRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDate lastPeriodStart;
    private LocalDate dueDate;
    private LocalDate conceptionDate;
    private Integer currentWeek;
    private Integer currentDay;
    private String pregnancyStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String endReason;
    private Long babyId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
