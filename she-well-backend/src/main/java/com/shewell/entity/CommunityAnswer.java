package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("community_answer")
public class CommunityAnswer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long questionId;
    private Long userId;
    private String content;
    private String images;     // JSON数组
    private Integer likeCount;
    private Boolean isAdopted;
    private LocalDateTime adoptedTime;
    private String status;    // published/hidden/deleted
    private Long expertId;    // 回答者是认证专家时
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
