package com.tunan.guagua.post.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.common.security.UserContext;
import com.tunan.guagua.post.dto.PostStatsVO;
import com.tunan.guagua.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {
    private final PostService postService;

    @GetMapping("/overview")
    public ApiResponse<PostStatsVO> overview() {
        UserContext.requireAdmin();
        return ApiResponse.success(postService.stats());
    }
}
