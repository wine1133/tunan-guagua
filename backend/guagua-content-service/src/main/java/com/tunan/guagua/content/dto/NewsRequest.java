package com.tunan.guagua.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewsRequest(
        @NotBlank(message = "请输入资讯标题")
        @Size(max = 200, message = "资讯标题最多 200 个字符") String title,
        @Size(max = 500, message = "摘要最多 500 个字符") String summary,
        @NotBlank(message = "请输入资讯正文") String content,
        String cover,
        @Size(max = 60, message = "城市名称过长") String city,
        @NotNull(message = "请选择发布状态") Integer status
) {
}
