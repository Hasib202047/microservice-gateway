package com.microservice.gatewayServer.jwt;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public TokenBlacklistService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String BLACKLIST_PREFIX = "blacklisted_token:";

    @PostConstruct
    public void checkRedisTemplate() {
        System.out.println("RedisTemplate: " + redisTemplate);
        redisTemplate.opsForValue().set("testKey", "testValue");
        System.out.println("Test value set: " + redisTemplate.opsForValue().get("testKey"));
    }
    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
    }
}
