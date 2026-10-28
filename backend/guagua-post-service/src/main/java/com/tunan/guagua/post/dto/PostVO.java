package com.tunan.guagua.post.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostVO(
        Long id,
        String title,
        String content,
        List<String> images,
        Long circleId,
        String circleName,
        Long topicId,
        String topicName,
        Long authorId,
        String authorName,
        String authorAvatar,
        Integer auditStatus,
        Integer likeCount,
        Integer favoriteCount,
        Integer commentCount,
        Integer viewCount,
        boolean liked,
        boolean favorited,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
