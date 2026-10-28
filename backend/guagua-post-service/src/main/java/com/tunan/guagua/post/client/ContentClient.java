package com.tunan.guagua.post.client;

import com.tunan.guagua.common.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "guagua-content-service")
public interface ContentClient {
    @GetMapping("/api/circles/batch")
    ApiResponse<List<ContentBrief>> batchCircles(@RequestParam("ids") String ids);

    @GetMapping("/api/topics/batch")
    ApiResponse<List<ContentBrief>> batchTopics(@RequestParam("ids") String ids);
}
