package com.example.ecommerce.cart.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class GuestCartRepository {

    private static final String PREFIX = "guest:cart";
    private static final long TTL_DAYS = 7;

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String sessionId, Map<Long, Integer> items) {
        redisTemplate.opsForValue().set(
                buildKey(sessionId),
                items,
                TTL_DAYS,
                TimeUnit.DAYS
        );
    }

    @SuppressWarnings("unchecked")
    public Map<Long, Integer> find(String sessionId) {
        return (Map<Long, Integer>) redisTemplate
                .opsForValue()
                .get(buildKey(sessionId));
    }

    public void delete(String sessionId) {
        redisTemplate.delete(buildKey(sessionId));
    }


    // helper
    private String buildKey(String sessionId) {
        return PREFIX + ":" + sessionId;
    }
}
