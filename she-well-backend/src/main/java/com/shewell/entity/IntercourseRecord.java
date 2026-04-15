package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("intercourse_record")
public class IntercourseRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDate intercourseDate;
    private Integer conceptionPossible;
    private Integer conceptionSource;
    private String notes;
    private LocalDateTime createTime;
}
