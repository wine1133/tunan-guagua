package com.tunan.guagua.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TopicRequest(
        @NotBlank(message = "请输入话题名称")
        @Size(max = 100, message = "话题名称最多 100 个字符")
        String name,
        @Size(max = 500, message = "封面地址过长") String cover,
        @Size(max = 500, message = "话题介绍最多 500 个字符") String description
) {
}
