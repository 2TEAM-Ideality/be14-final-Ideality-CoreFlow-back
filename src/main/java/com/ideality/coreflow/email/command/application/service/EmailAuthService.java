package com.ideality.coreflow.email.command.application.service;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.infra.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthService {
    private final RedisUtil redisUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String EMAIL_AUTH_PREFIX = "emailAuth:";
    private static final String VERIFY_CODE_PREFIX = "emailAuthVerified:";
    private static final long EMAIL_AUTH_TTL_MINUTES = 10;

    // 레디스에 저장할 키 이름 설정
    private String generateAuthKey(String email) {
        return EMAIL_AUTH_PREFIX + email;
    }

    // 레디스에 등록된 이메일인지 확인
    public boolean isExistEmail(String email) {
        String key = generateAuthKey(email);
        String value = redisTemplate.opsForValue().get(key);

        if(value == null) {
            log.info("신규 이메일");
            return false;
        } else {
            log.info("이미 가입된 이메일");
            return true;
        }
    }
}
