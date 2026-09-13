package com.example.ecommerce.security.session;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;

@Repository
@RequiredArgsConstructor
public class RedisSessionStore implements SessionStore {

    private static final String SESSION_PREFIX = "session:";
    private static final String FIELD_USER_ID = "userId";
    private static final String FIELD_TOKEN_HASH = "refreshTokenHash";
    private static final String FIELD_CREATED_AT = "createdAt";
    private static final String FIELD_ABS_EXPIRES = "absoluteExpiresAt";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String sessionId, Long userId, String refreshTokenHash, Instant createdAt, Instant absoluteExpiresAt, long ttlSeconds) {

        String key = SESSION_PREFIX + sessionId;

        Map<String, String> fields = new HashMap<>();

        fields.put(FIELD_USER_ID, userId.toString());
        fields.put(FIELD_TOKEN_HASH, refreshTokenHash);
        fields.put(FIELD_CREATED_AT, createdAt.toString());
        fields.put(FIELD_ABS_EXPIRES, absoluteExpiresAt.toString());

        redisTemplate.opsForHash().putAll(key, fields);

        redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public SessionData find(String sessionId) {

        String key = SESSION_PREFIX + sessionId;

        Map<Object, Object> fields = redisTemplate.opsForHash().entries(key);

        if (fields.isEmpty()) {
            return null;
        }

        return new SessionData(Long.valueOf((String) fields.get(FIELD_USER_ID)), (String) fields.get(FIELD_TOKEN_HASH), Instant.parse((String) fields.get(FIELD_CREATED_AT)), Instant.parse((String) fields.get(FIELD_ABS_EXPIRES)));
    }


    @Override
    public boolean rotate(String sessionId, String oldTokenHash, String newTokenHash, long ttlSeconds) {

        String key = SESSION_PREFIX + sessionId;

        String script = """
                local currentHash = redis.call('HGET', KEYS[1], ARGV[1])
                
                if not currentHash then
                    return 0
                end
                
                if currentHash ~= ARGV[2] then
                    return 0
                end
                
                redis.call('HSET', KEYS[1], ARGV[1], ARGV[3])
                redis.call('EXPIRE', KEYS[1], ARGV[4])
                
                return 1
                """;

        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(key), FIELD_TOKEN_HASH, oldTokenHash, newTokenHash, String.valueOf(ttlSeconds));

        return Long.valueOf(1).equals(result);
    }


    @Override
    public void delete(String sessionId) {

        redisTemplate.delete(SESSION_PREFIX + sessionId);
    }


    @Override
    public boolean exists(String sessionId) {

        return Boolean.TRUE.equals(redisTemplate.hasKey(SESSION_PREFIX + sessionId));
    }

}
