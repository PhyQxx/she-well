package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("checkup_record")
public class CheckupRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long pregnancyId;
    private LocalDate checkupDate;
    private Integer gestationalWeek;
    private String hospital;
    private String doctor;
    private String diagnosis;
    private String attachments;
    private LocalDate nextAppointment;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
