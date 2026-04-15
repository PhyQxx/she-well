package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reminder")
public class Reminder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String type;         // period / ovulation / checkup / medicine / sleep / exercise / water / custom
    private String title;
    private String content;
    private String remindTime;   // HH:mm
    private Integer remindDays; // 提前几天提醒
    private Boolean enabled;     // 是否启用
    private Integer cyclePhase;  // 0=每天 1=经期 2=排卵期 3=黄体期
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
