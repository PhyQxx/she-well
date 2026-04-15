package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_role_history")
public class UserRoleHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String fromRole;
    private String toRole;
    private LocalDateTime switchTime;
    private LocalDateTime createTime;
}
