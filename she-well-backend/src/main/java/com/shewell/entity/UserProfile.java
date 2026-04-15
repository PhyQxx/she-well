package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_profile")
public class UserProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String nickname;
    private LocalDate birthday;
    private BigDecimal height;
    private BigDecimal weight;
    private String bloodType;
    private Integer menstrualCycle;
    private Integer periodDuration;
    private LocalDate lastPeriodDate;
    private String avatarUrl;
    private String intro;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
