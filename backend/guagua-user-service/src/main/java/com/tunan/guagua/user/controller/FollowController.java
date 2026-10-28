package com.tunan.guagua.user.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.common.security.UserContext;
import com.tunan.guagua.user.dto.FollowUserVO;
import com.tunan.guagua.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{userId}")
    public ApiResponse<Void> follow(@PathVariable Long userId) {
        followService.follow(UserContext.requireUserId(), userId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> unfollow(@PathVariable Long userId) {
        followService.unfollow(UserContext.requireUserId(), userId);
        return ApiResponse.success();
    }

    @GetMapping("/{userId}/status")
    public ApiResponse<Map<String, Boolean>> status(@PathVariable Long userId) {
        return ApiResponse.success(Map.of("following", followService.isFollowing(UserContext.getUserId(), userId)));
    }

    @GetMapping("/me/following")
    public ApiResponse<List<FollowUserVO>> following() {
        return ApiResponse.success(followService.following(UserContext.requireUserId()));
    }

    @GetMapping("/me/followers")
    public ApiResponse<List<FollowUserVO>> followers() {
        return ApiResponse.success(followService.followers(UserContext.requireUserId()));
    }
}
