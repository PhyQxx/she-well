package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("checkin_record")
public class CheckinRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long habitId;
    private LocalDate checkinDate;
    private String value;
    private String symptom;
    private String notes;
    private LocalDateTime createTime;
    private String mood;
    private String emotion;
    private String sleepQuality;
    private String exerciseType;
}
