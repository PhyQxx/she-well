package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("habit")
public class Habit {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String icon;
    private String targetType;
    private String targetValue;
    private String color;
    private String reminderTime;
    private Integer reminderEnabled;
    private String category;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
