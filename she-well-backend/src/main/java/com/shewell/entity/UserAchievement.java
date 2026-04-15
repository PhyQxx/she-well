package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_achievement")
public class UserAchievement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long achievementId;
    private Integer status;
    private Integer progressCurrent;
    private Integer progressTarget;
    private LocalDateTime earnedTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
