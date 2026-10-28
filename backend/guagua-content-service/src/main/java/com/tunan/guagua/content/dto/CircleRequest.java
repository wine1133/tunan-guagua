package com.tunan.guagua.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CircleRequest(
        @NotBlank(message = "请输入圈子名称")
        @Size(max = 80, message = "圈子名称最多 80 个字符")
        String name,
        @Size(max = 500, message = "封面地址过长") String cover,
        @Size(max = 500, message = "圈子介绍最多 500 个字符") String description
) {
}
