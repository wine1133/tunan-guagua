package com.tunan.guagua.content.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.security.UserContext;
import com.tunan.guagua.content.dto.AuditRequest;
import com.tunan.guagua.content.dto.TopicRequest;
import com.tunan.guagua.content.entity.Topic;
import com.tunan.guagua.content.service.TopicService;
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
public class TopicController {
    private final TopicService topicService;

    @GetMapping("/topics")
    public ApiResponse<PageResult<Topic>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "12") long size,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(topicService.publicList(page, size, keyword));
    }

    @GetMapping("/topics/mine")
    public ApiResponse<List<Topic>> mine() {
        return ApiResponse.success(topicService.mine(UserContext.requireUserId()));
    }

    @GetMapping("/topics/{id}")
    public ApiResponse<Topic> detail(@PathVariable Long id) {
        return ApiResponse.success(topicService.detail(id));
    }

    @PostMapping("/topics")
    public ApiResponse<Topic> create(@Valid @RequestBody TopicRequest request) {
        UserContext.requireUserId();
        return ApiResponse.success(topicService.create(UserContext.get(), request));
    }

    @PutMapping("/topics/{id}")
    public ApiResponse<Topic> update(@PathVariable Long id, @Valid @RequestBody TopicRequest request) {
        UserContext.requireUserId();
        return ApiResponse.success(topicService.update(id, UserContext.get(), request));
    }

    @DeleteMapping("/topics/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        UserContext.requireUserId();
        topicService.delete(id, UserContext.get());
        return ApiResponse.success();
    }

    @GetMapping("/admin/topics")
    public ApiResponse<PageResult<Topic>> adminList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer auditStatus) {
        UserContext.requireAdmin();
        return ApiResponse.success(topicService.adminList(page, size, keyword, auditStatus));
    }

    @PutMapping("/admin/topics/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id, @Valid @RequestBody AuditRequest request) {
        UserContext.requireAdmin();
        topicService.audit(id, request.auditStatus());
        return ApiResponse.success();
    }
}
