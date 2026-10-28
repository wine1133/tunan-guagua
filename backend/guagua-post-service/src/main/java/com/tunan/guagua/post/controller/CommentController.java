package com.tunan.guagua.post.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.security.UserContext;
import com.tunan.guagua.post.dto.CommentRequest;
import com.tunan.guagua.post.dto.CommentVO;
import com.tunan.guagua.post.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/post/{postId}")
    public ApiResponse<List<CommentVO>> list(@PathVariable Long postId) {
        return ApiResponse.success(commentService.listByPost(postId));
    }

    @PostMapping
    public ApiResponse<CommentVO> create(@Valid @RequestBody CommentRequest request) {
        UserContext.requireUserId();
        return ApiResponse.success(commentService.create(UserContext.get(), request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        UserContext.requireUserId();
        commentService.delete(id, UserContext.get());
        return ApiResponse.success();
    }

    @GetMapping("/mine")
    public ApiResponse<PageResult<CommentVO>> mine(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.success(commentService.mine(UserContext.requireUserId(), page, size));
    }
}
