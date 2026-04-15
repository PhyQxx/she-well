package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("discovery")
public class Discovery {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String subTitle;
    private String coverImage;
    private String linkUrl;
    private String linkType;      // article/post/activity|url
    private Integer sortOrder;
    private String status;        // enabled/disabled
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
}
