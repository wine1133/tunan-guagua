package com.tunan.guagua.content.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.model.LoginUser;
import com.tunan.guagua.common.security.UserContext;
import com.tunan.guagua.content.dto.AuditRequest;
import com.tunan.guagua.content.dto.CircleRequest;
import com.tunan.guagua.content.entity.Circle;
import com.tunan.guagua.content.service.CircleService;
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

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CircleController {
    private final CircleService circleService;

    @GetMapping("/circles")
    public ApiResponse<PageResult<Circle>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "12") long size,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(circleService.publicList(page, size, keyword));
    }

    @GetMapping("/circles/mine")
    public ApiResponse<List<Circle>> mine() {
        return ApiResponse.success(circleService.mine(UserContext.requireUserId()));
    }

    @GetMapping("/circles/{id}")
    public ApiResponse<Circle> detail(@PathVariable Long id) {
        return ApiResponse.success(circleService.detail(id));
    }

    @PostMapping("/circles")
    public ApiResponse<Circle> create(@Valid @RequestBody CircleRequest request) {
        UserContext.requireUserId();
        return ApiResponse.success(circleService.create(UserContext.get(), request));
    }

    @PutMapping("/circles/{id}")
    public ApiResponse<Circle> update(@PathVariable Long id, @Valid @RequestBody CircleRequest request) {
        UserContext.requireUserId();
        return ApiResponse.success(circleService.update(id, UserContext.get(), request));
    }

    @DeleteMapping("/circles/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        UserContext.requireUserId();
        circleService.delete(id, UserContext.get());
        return ApiResponse.success();
    }

    @GetMapping("/admin/circles")
    public ApiResponse<PageResult<Circle>> adminList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer auditStatus) {
        UserContext.requireAdmin();
        return ApiResponse.success(circleService.adminList(page, size, keyword, auditStatus));
    }

    @PutMapping("/admin/circles/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id, @Valid @RequestBody AuditRequest request) {
        UserContext.requireAdmin();
        circleService.audit(id, request.auditStatus());
        return ApiResponse.success();
    }
}
