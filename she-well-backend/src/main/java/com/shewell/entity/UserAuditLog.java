package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_audit_log")
public class UserAuditLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String actionType;
    private String targetType;
    private Long targetId;
    private String ip;
    private String userAgent;
    private String detail;
    private LocalDateTime createTime;
}
