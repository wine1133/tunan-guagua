package com.tunan.guagua.content.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.security.UserContext;
import com.tunan.guagua.content.dto.NewsRequest;
import com.tunan.guagua.content.entity.News;
import com.tunan.guagua.content.service.NewsService;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/news")
    public ApiResponse<PageResult<News>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(newsService.publicList(page, size, keyword));
    }

    @GetMapping("/news/{id}")
    public ApiResponse<News> detail(@PathVariable Long id) {
        return ApiResponse.success(newsService.detail(id));
    }

    @GetMapping("/admin/news")
    public ApiResponse<PageResult<News>> adminList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword) {
        UserContext.requireAdmin();
        return ApiResponse.success(newsService.adminList(page, size, keyword));
    }

    @PostMapping("/admin/news")
    public ApiResponse<News> create(@Valid @RequestBody NewsRequest request) {
        UserContext.requireAdmin();
        return ApiResponse.success(newsService.save(null, request));
    }

    @PutMapping("/admin/news/{id}")
    public ApiResponse<News> update(@PathVariable Long id, @Valid @RequestBody NewsRequest request) {
        UserContext.requireAdmin();
        return ApiResponse.success(newsService.save(id, request));
    }

    @DeleteMapping("/admin/news/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        UserContext.requireAdmin();
        newsService.delete(id);
        return ApiResponse.success();
    }
}
