package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("expert_question")
public class ExpertQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long expertId;
    private String category;
    private String title;
    private String content;
    private String images;
    private String status;
    private Integer isAnonymous;
    private Integer viewCount;
    private Integer answerCount;
    private Long adoptedAnswerId;
    private Integer rewardPoints;
    private LocalDateTime expireTime;
    private LocalDateTime closedAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
