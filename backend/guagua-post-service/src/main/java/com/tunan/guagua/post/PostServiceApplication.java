package com.tunan.guagua.post;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.tunan.guagua")
@MapperScan("com.tunan.guagua.post.mapper")
@EnableFeignClients(basePackages = "com.tunan.guagua.post.client")
public class PostServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PostServiceApplication.class, args);
    }
}
