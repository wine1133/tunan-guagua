package com.tunan.guagua.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("topics")
public class Topic {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String cover;
    private String description;
    private Integer auditStatus;
    private Integer heat;
    private Integer postCount;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
