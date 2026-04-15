package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("achievement")
public class Achievement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    private String description;
    private String icon;
    private String category;
    private String conditionType;
    private String conditionValue;
    private Integer rewardPoints;
    private Integer displayOrder;
    private Integer version;
    private LocalDateTime createTime;
}
