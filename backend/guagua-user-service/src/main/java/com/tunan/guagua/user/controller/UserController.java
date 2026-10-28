package com.tunan.guagua.user.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.security.UserContext;
import com.tunan.guagua.user.dto.StatusRequest;
import com.tunan.guagua.user.dto.UpdateProfileRequest;
import com.tunan.guagua.user.dto.UserStatsVO;
import com.tunan.guagua.user.dto.UserVO;
import com.tunan.guagua.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users/me")
    public ApiResponse<UserVO> me() {
        return ApiResponse.success(userService.getById(UserContext.requireUserId()));
    }

    @PutMapping("/users/me")
    public ApiResponse<UserVO> updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.success(userService.updateProfile(UserContext.requireUserId(), request));
    }

    @GetMapping("/users/batch")
    public ApiResponse<List<UserVO>> batch(@RequestParam String ids) {
        List<Long> parsed = Arrays.stream(ids.split(","))
                .filter(value -> !value.isBlank())
                .map(Long::valueOf)
                .toList();
        return ApiResponse.success(userService.batch(parsed));
    }

    @GetMapping("/users/{id}")
    public ApiResponse<UserVO> detail(@PathVariable Long id) {
        return ApiResponse.success(userService.getById(id));
    }

    @GetMapping("/admin/users")
    public ApiResponse<PageResult<UserVO>> adminList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        UserContext.requireAdmin();
        return ApiResponse.success(userService.adminList(page, size, keyword, status));
    }

    @PutMapping("/admin/users/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        UserContext.requireAdmin();
        userService.updateStatus(id, request.status());
        return ApiResponse.success();
    }

    @GetMapping("/admin/users/stats")
    public ApiResponse<UserStatsVO> adminStats() {
        UserContext.requireAdmin();
        return ApiResponse.success(userService.adminStats());
    }
}
