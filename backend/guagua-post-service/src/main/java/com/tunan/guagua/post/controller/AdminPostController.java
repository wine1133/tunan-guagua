package com.tunan.guagua.post.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.security.UserContext;
import com.tunan.guagua.post.dto.AuditRequest;
import com.tunan.guagua.post.dto.PostVO;
import com.tunan.guagua.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
public class AdminPostController {
    private final PostService postService;

    @GetMapping
    public ApiResponse<PageResult<PostVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer auditStatus) {
        var admin = UserContext.requireAdmin();
        return ApiResponse.success(postService.adminList(page, size, keyword, auditStatus, admin.id()));
    }

    @GetMapping("/{id}")
    public ApiResponse<PostVO> detail(@PathVariable Long id) {
        UserContext.requireAdmin();
        return ApiResponse.success(postService.detail(id, UserContext.getUserId(), true));
    }

    @PutMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id, @Valid @RequestBody AuditRequest request) {
        UserContext.requireAdmin();
        postService.audit(id, request.auditStatus());
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        UserContext.requireAdmin();
        postService.delete(id, UserContext.get());
        return ApiResponse.success();
    }
}
