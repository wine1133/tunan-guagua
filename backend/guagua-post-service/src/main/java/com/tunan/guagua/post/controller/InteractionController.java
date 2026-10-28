package com.tunan.guagua.post.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.security.UserContext;
import com.tunan.guagua.post.dto.PostVO;
import com.tunan.guagua.post.service.InteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InteractionController {
    private final InteractionService interactionService;

    @PostMapping("/likes/{postId}")
    public ApiResponse<Void> like(@PathVariable Long postId) {
        interactionService.like(postId, UserContext.requireUserId());
        return ApiResponse.success();
    }

    @DeleteMapping("/likes/{postId}")
    public ApiResponse<Void> unlike(@PathVariable Long postId) {
        interactionService.unlike(postId, UserContext.requireUserId());
        return ApiResponse.success();
    }

    @GetMapping("/likes/mine")
    public ApiResponse<PageResult<PostVO>> myLikes(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.success(interactionService.myLikes(UserContext.requireUserId(), page, size));
    }

    @PostMapping("/favorites/{postId}")
    public ApiResponse<Void> favorite(@PathVariable Long postId) {
        interactionService.favorite(postId, UserContext.requireUserId());
        return ApiResponse.success();
    }

    @DeleteMapping("/favorites/{postId}")
    public ApiResponse<Void> unfavorite(@PathVariable Long postId) {
        interactionService.unfavorite(postId, UserContext.requireUserId());
        return ApiResponse.success();
    }

    @GetMapping("/favorites/mine")
    public ApiResponse<PageResult<PostVO>> myFavorites(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.success(interactionService.myFavorites(UserContext.requireUserId(), page, size));
    }
}
