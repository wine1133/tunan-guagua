package com.tunan.guagua.content.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.common.security.UserContext;
import com.tunan.guagua.content.dto.ContentStatsVO;
import com.tunan.guagua.content.service.ContentStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/content")
@RequiredArgsConstructor
public class ContentStatsController {
    private final ContentStatsService contentStatsService;

    @GetMapping("/stats")
    public ApiResponse<ContentStatsVO> stats() {
        UserContext.requireAdmin();
        return ApiResponse.success(contentStatsService.stats());
    }
}
