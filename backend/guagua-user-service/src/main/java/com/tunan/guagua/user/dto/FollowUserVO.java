package com.tunan.guagua.user.dto;

import com.tunan.guagua.user.entity.User;

import java.time.LocalDateTime;

public record FollowUserVO(
        Long id,
        String nickname,
        String avatar,
        String bio,
        String city,
        LocalDateTime followedAt
) {
    public static FollowUserVO from(User user, LocalDateTime followedAt) {
        return new FollowUserVO(user.getId(), user.getNickname(), user.getAvatar(), user.getBio(), user.getCity(), followedAt);
    }
}
