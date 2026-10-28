package com.tunan.guagua.post.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AuditRequest(
        @NotNull(message = "请选择审核状态")
        @Min(value = 0, message = "审核状态不正确")
        @Max(value = 2, message = "审核状态不正确")
        Integer auditStatus
) {
}
