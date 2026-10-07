package com.example.ecommerce.cart.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GuestCartRepository {

    @Value("${app.redis.guest-cart.prefix}")
    private String prefix;

    @Value("${app.redis.guest-cart.ttl-days}")
    private long ttlDays;

    private final StringRedisTemplate redisTemplate;

    public void addItem(String sessionId, Long variantId, Integer quantity) {
        String key = buildKey(sessionId);

        redisTemplate.opsForHash().increment(
                key,
                variantId.toString(),
                quantity
        );

        redisTemplate.expire(key, ttlDays, TimeUnit.DAYS);

    }

    public void updateItem(
            String sessionId, Long variantId, Integer quantity) {
        String key = buildKey(sessionId);
        redisTemplate.opsForHash().put(
                key,
                variantId.toString(),
                quantity.toString()
        );

        redisTemplate.expire(key, ttlDays, TimeUnit.DAYS);
    }

    public void removeItem(String sessionId, Long variantId) {
        String key = buildKey(sessionId);

        redisTemplate.opsForHash().delete(key, variantId.toString()
        );

        Long size = redisTemplate.opsForHash().size(key);

        if (size != null && size == 0) {
            redisTemplate.delete(key);
        }
    }

    public Map<Long, Integer> find(String sessionId) {
        Map<Object, Object> values =
                redisTemplate.opsForHash().entries(buildKey(sessionId));

        return values.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> Long.valueOf(entry.getKey().toString()),
                        entry -> Integer.valueOf(entry.getValue().toString())
                ));
    }


    public void delete(String sessionId) {
        redisTemplate.delete(buildKey(sessionId));
    }


    // helper
    private String buildKey(String sessionId) {
        return prefix  + ":" + sessionId;
    }
}
