package com.tunan.guagua.post.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.security.UserContext;
import com.tunan.guagua.post.dto.InteractionVO;
import com.tunan.guagua.post.service.InteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminInteractionController {
    private final InteractionService interactionService;

    @GetMapping("/likes")
    public ApiResponse<PageResult<InteractionVO>> likes(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        UserContext.requireAdmin();
        return ApiResponse.success(interactionService.adminLikes(page, size));
    }

    @GetMapping("/favorites")
    public ApiResponse<PageResult<InteractionVO>> favorites(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        UserContext.requireAdmin();
        return ApiResponse.success(interactionService.adminFavorites(page, size));
    }
}
