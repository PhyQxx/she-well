package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String type;
    private String coverImage;
    private String description;
    private String rules;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer rewardPoints;
    private Integer participantCount;
    private Integer maxParticipants;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
