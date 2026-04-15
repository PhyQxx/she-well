package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("post_audit_log")
public class PostAuditLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;
    private Long auditorId;
    private String action;        // approve/reject
    private String reason;
    private LocalDateTime createTime;
}
