package com.tunan.guagua.post.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.security.UserContext;
import com.tunan.guagua.post.dto.PostRequest;
import com.tunan.guagua.post.dto.PostVO;
import com.tunan.guagua.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public ApiResponse<PageResult<PostVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long circleId,
            @RequestParam(required = false) Long topicId) {
        return ApiResponse.success(postService.publicList(page, size, keyword, circleId, topicId, UserContext.getUserId()));
    }

    @GetMapping("/mine")
    public ApiResponse<PageResult<PostVO>> mine(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.success(postService.mine(UserContext.requireUserId(), page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<PostVO> detail(@PathVariable Long id) {
        return ApiResponse.success(postService.detail(id, UserContext.getUserId(), false));
    }

    @PostMapping
    public ApiResponse<PostVO> create(@Valid @RequestBody PostRequest request) {
        UserContext.requireUserId();
        return ApiResponse.success(postService.create(UserContext.get(), request));
    }

    @PutMapping("/{id}")
    public ApiResponse<PostVO> update(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        UserContext.requireUserId();
        return ApiResponse.success(postService.update(id, UserContext.get(), request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        UserContext.requireUserId();
        postService.delete(id, UserContext.get());
        return ApiResponse.success();
    }
}
