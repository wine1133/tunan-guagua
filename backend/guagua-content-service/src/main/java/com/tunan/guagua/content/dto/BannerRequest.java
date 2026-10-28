package com.tunan.guagua.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BannerRequest(
        @NotBlank(message = "请输入轮播图标题") String title,
        @NotBlank(message = "请输入图片地址") String imageUrl,
        String linkUrl,
        @NotNull(message = "请输入排序值") Integer sort,
        @NotNull(message = "请选择显示状态") Integer status
) {
}
