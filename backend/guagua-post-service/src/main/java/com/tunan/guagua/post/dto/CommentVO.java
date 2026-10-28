package com.tunan.guagua.post.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentVO(
        Long id,
        Long postId,
        String postTitle,
        Long userId,
        String userName,
        String userAvatar,
        Long parentId,
        String content,
        LocalDateTime createdAt
) {
}
