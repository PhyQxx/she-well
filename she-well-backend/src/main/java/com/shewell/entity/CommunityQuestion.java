package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("community_question")
public class CommunityQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String images;     // JSON数组
    private String category;   // period/ovulation/pregnancy/baby/emotion/other
    private String status;     // pending/published/closed/deleted
    private Boolean isAnonymous;
    private Boolean isTop;
    private Boolean isEssence;
    private Integer viewCount;
    private Integer answerCount;
    private Integer likeCount;
    private Integer collectCount;
    private Long acceptedAnswerId;
    private LocalDateTime lastAnswerTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
