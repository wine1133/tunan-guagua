package com.tunan.guagua.post.config;

import cn.hutool.core.util.StrUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(
            @Value("${spring.data.redis.host:localhost}") String host,
            @Value("${spring.data.redis.port:6380}") int port,
            @Value("${spring.data.redis.password:}") String password) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setDatabase(0)
                .setConnectionMinimumIdleSize(2)
                .setConnectionPoolSize(8);
        if (StrUtil.isNotBlank(password)) {
            config.useSingleServer().setPassword(password);
        }
        return Redisson.create(config);
    }
}
