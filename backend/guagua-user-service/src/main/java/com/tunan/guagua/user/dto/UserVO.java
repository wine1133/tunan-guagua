package com.tunan.guagua.user.dto;

import com.tunan.guagua.user.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserVO(
        Long id,
        String username,
        String nickname,
        String avatar,
        String bio,
        String city,
        String role,
        Integer status,
        LocalDateTime createdAt
) {
    public static UserVO from(User user) {
        if (user == null) {
            return null;
        }
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .city(user.getCity())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
