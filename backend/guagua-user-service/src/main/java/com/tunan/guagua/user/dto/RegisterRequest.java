package com.tunan.guagua.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "请输入账号")
        @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "账号需为 4-20 位字母、数字或下划线")
        String username,
        @NotBlank(message = "请输入昵称")
        @Size(max = 20, message = "昵称最多 20 个字符")
        String nickname,
        @NotBlank(message = "请输入密码")
        @Size(min = 6, max = 30, message = "密码需为 6-30 位")
        String password
) {
}
