package com.tunan.guagua.post.client;

import com.tunan.guagua.common.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "guagua-user-service")
public interface UserClient {
    @GetMapping("/api/users/batch")
    ApiResponse<List<UserBrief>> batch(@RequestParam("ids") String ids);
}
