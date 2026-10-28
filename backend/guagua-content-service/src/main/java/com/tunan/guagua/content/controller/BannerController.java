package com.tunan.guagua.content.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.security.UserContext;
import com.tunan.guagua.content.dto.BannerRequest;
import com.tunan.guagua.content.entity.Banner;
import com.tunan.guagua.content.service.BannerService;
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
public class BannerController {
    private final BannerService bannerService;

    @GetMapping("/banners")
    public ApiResponse<List<Banner>> enabled() {
        return ApiResponse.success(bannerService.enabledList());
    }

    @GetMapping("/admin/banners")
    public ApiResponse<PageResult<Banner>> adminList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        UserContext.requireAdmin();
        return ApiResponse.success(bannerService.adminList(page, size));
    }

    @PostMapping("/admin/banners")
    public ApiResponse<Banner> create(@Valid @RequestBody BannerRequest request) {
        UserContext.requireAdmin();
        return ApiResponse.success(bannerService.save(null, request));
    }

    @PutMapping("/admin/banners/{id}")
    public ApiResponse<Banner> update(@PathVariable Long id, @Valid @RequestBody BannerRequest request) {
        UserContext.requireAdmin();
        return ApiResponse.success(bannerService.save(id, request));
    }

    @DeleteMapping("/admin/banners/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        UserContext.requireAdmin();
        bannerService.delete(id);
        return ApiResponse.success();
    }
}
