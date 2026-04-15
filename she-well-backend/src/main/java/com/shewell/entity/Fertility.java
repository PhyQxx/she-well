package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fertility_record")
public class Fertility {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDate recordDate;
    private String level;          // low / medium / high
    private Double score;          // 0-100 生育力评分
    private String factors;        // 影响因素，JSON数组
    private String suggestions;    // 改善建议，JSON数组
    private String cycleLength;    // 周期长度参考
    private String ovulationStatus; // 排卵状况描述
    private String conceptionChance; // 受孕几率: low/medium/high
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
