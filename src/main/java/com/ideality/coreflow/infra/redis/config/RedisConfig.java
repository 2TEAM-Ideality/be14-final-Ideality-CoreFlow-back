package com.ideality.coreflow.infra.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfig {

    /* 설명. 명시적으로 호스트와 포트 번호 설정 */
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    // yml파일에 기입한 host, port 로 redis server 와 연결한 lettuce 클라이언트 객체 생성
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("[RedisConfig] Redis 연결 설정 시작 - host: {}, port: {}", host, port);
        return new LettuceConnectionFactory(host, port);
    }
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        log.info("[RedisConfig] RedisTemplate 생성");
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
