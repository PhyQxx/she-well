package com.shewell.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("expert_answer")
public class ExpertAnswer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long questionId;
    private Long expertId;
    private String content;
    private String images;
    private Integer isAdopted;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
