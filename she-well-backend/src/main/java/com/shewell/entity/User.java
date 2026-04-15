package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String avatar;
    private Integer status;           // 1=正常 0=禁用 -1=删除
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String nickname;
    private String currentMode;       // period/trying/pregnancy/nursing
}
