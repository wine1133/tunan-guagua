package com.tunan.guagua.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("banners")
public class Banner {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private Integer sort;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
