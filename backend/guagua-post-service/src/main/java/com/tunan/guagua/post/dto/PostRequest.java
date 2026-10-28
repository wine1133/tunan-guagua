package com.tunan.guagua.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostRequest(
        @NotBlank(message = "请输入帖子标题")
        @Size(max = 160, message = "帖子标题最多 160 个字符")
        String title,
        @NotBlank(message = "请输入帖子正文")
        String content,
        List<String> images,
        Long circleId,
        Long topicId
) {
}
