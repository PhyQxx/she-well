package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_mode")
public class Settings {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String currentMode;       // period / trying / pregnancy / nursing
    private Integer cycleLength;      // 周期天数，默认28
    private Integer periodDuration;  // 经期天数，默认5
    private LocalDate lastPeriodDate; // 最近经期开始日期
    private String remindTime;        // 提醒时间 HH:mm
    private Boolean notifyEnabled;   // 通知开关
    private Integer cycleReminderDays; // 提前几天提醒
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
