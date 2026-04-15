package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("expert")
public class Expert {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String realName;
    private String specialty;
    private String hospital;
    private String department;
    private String professionalTitle;
    private String licenseImage;
    private String certificationStatus;
    private String intro;
    private String avatarUrl;
    private Integer answerCount;
    private Integer followCount;
    private BigDecimal rating;
    private Integer status;
    private LocalDateTime applyTime;
    private LocalDateTime certifyTime;
    private String rejectReason;
    private Integer rejectCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
