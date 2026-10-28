package com.tunan.guagua.post.dto;

import java.time.LocalDateTime;

public record InteractionVO(
        Long id,
        Long postId,
        String postTitle,
        Long userId,
        String userName,
        LocalDateTime createdAt
) {
}
