package com.ideality.coreflow.infra.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Redis에 데이터 저장 (TTL 설정 가능)
     */
    public void set(String key, String value, long timeoutInMinutes) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, value, Duration.ofMinutes(timeoutInMinutes));
    }

    /**
     * Redis에 데이터 저장 (TTL 없이 무한 저장)
     */
    public void set(String key, String value) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, value);
    }
    /**
     * Redis에서 데이터 가져오기
     */
    public String get(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    /**
     * Redis에서 데이터 삭제
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Redis에 키가 존재하는지 확인
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
