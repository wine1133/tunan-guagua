package com.tunan.guagua.content.controller;

import com.tunan.guagua.common.api.ApiResponse;
import com.tunan.guagua.content.dto.ContentBrief;
import com.tunan.guagua.content.mapper.CircleMapper;
import com.tunan.guagua.content.mapper.TopicMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BatchContentController {
    private final CircleMapper circleMapper;
    private final TopicMapper topicMapper;

    @GetMapping("/circles/batch")
    public ApiResponse<List<ContentBrief>> circles(@RequestParam String ids) {
        List<Long> parsed = parse(ids);
        return ApiResponse.success(circleMapper.selectBatchIds(parsed).stream()
                .map(item -> new ContentBrief(item.getId(), item.getName(), item.getCover(), item.getDescription()))
                .toList());
    }

    @GetMapping("/topics/batch")
    public ApiResponse<List<ContentBrief>> topics(@RequestParam String ids) {
        List<Long> parsed = parse(ids);
        return ApiResponse.success(topicMapper.selectBatchIds(parsed).stream()
                .map(item -> new ContentBrief(item.getId(), item.getName(), item.getCover(), item.getDescription()))
                .toList());
    }

    private List<Long> parse(String ids) {
        return Arrays.stream(ids.split(","))
                .filter(value -> !value.isBlank())
                .map(Long::valueOf)
                .toList();
    }
}
