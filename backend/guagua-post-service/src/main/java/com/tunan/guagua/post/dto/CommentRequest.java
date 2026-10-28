package com.tunan.guagua.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotNull(message = "帖子不能为空") Long postId,
        Long parentId,
        @NotBlank(message = "请输入评论内容")
        @Size(max = 1000, message = "评论最多 1000 个字符")
        String content
) {
}
