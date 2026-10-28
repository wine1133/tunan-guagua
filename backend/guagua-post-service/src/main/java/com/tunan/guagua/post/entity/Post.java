package com.tunan.guagua.post.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("posts")
public class Post {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String images;
    private Long circleId;
    private Long topicId;
    private Long authorId;
    private Integer auditStatus;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer commentCount;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
