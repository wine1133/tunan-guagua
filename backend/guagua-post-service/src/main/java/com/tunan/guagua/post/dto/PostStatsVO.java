package com.tunan.guagua.post.dto;

public record PostStatsVO(
        long totalPosts,
        long publishedPosts,
        long pendingPosts,
        long totalComments,
        long totalLikes,
        long totalFavorites,
        long todayPosts
) {
}
