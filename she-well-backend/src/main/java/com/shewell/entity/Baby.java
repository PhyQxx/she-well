package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("baby")
public class Baby {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private BigDecimal birthHeight;
    private BigDecimal birthWeight;
    private String deliveryType;
    private String bloodType;
    private Integer birthOrder;
    private Integer isPrimary;
    private String notes;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
