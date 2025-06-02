package com.ideality.coreflow.email.command.service;

import com.ideality.coreflow.infra.redis.util.RedisUtil;
import org.springframework.stereotype.Service;

@Service
public class EmailAuthService {
    private final RedisUtil redisUtil;

    public EmailAuthService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }
}
