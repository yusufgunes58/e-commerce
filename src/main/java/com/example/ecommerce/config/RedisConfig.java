package com.example.ecommerce.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Map;

@EnableCaching
@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory
    ) {
        RedisCacheConfiguration cacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(
                                                GenericJacksonJsonRedisSerializer
                                                        .builder()
                                                        .build()
                                        )
                        );

        RedisCacheConfiguration productListConfig =
                cacheConfiguration.entryTtl(
                        Duration.ofMinutes(10)
                );

        RedisCacheConfiguration productDetailtConfig =
                cacheConfiguration.entryTtl(
                        Duration.ofMinutes(10)
                );



        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .withInitialCacheConfigurations(
                        Map.of(
                                "productList", productListConfig,
                                "productDetail", productDetailtConfig
                        )
                )
                .build();
    }

}
