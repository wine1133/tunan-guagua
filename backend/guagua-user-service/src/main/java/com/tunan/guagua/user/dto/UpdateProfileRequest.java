package com.tunan.guagua.user.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 20, message = "昵称最多 20 个字符") String nickname,
        @Size(max = 500, message = "头像地址过长") String avatar,
        @Size(max = 300, message = "个人简介最多 300 个字符") String bio,
        @Size(max = 60, message = "城市名称过长") String city
) {
}
