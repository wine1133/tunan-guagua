package com.tunan.guagua.content.dto;

public record ContentStatsVO(
        long totalCircles,
        long totalTopics,
        long pendingCircles,
        long pendingTopics,
        long totalNews
) {
}
