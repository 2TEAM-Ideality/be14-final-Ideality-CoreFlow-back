package com.ideality.coreflow.auth.command.application.service;

import com.ideality.coreflow.auth.command.application.dto.response.TokenResponse;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.tenant.config.TenantContext;
import com.ideality.coreflow.security.jwt.JwtProvider;
import com.ideality.coreflow.security.jwt.JwtUtil;
import com.ideality.coreflow.user.command.application.dto.LoginDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    @Override
    public TokenResponse login(LoginDTO userInfo, String password, List<String> userOfRoles) {

        log.info("로그인 로직 시작");

        // 비밀번호 비교
        if (!passwordEncoder.matches(password, userInfo.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        log.info("비밀번호 매칭 완료");

        // accessToken 생성
        String accessToken = jwtProvider.generateAccessToken(userInfo.getId(), userInfo.getEmployeeNum(), TenantContext.getTenant(), userOfRoles);
        log.info("AccessToken 발급 완료: {}", accessToken);

        // refreshToken 생성
        String refreshToken = jwtProvider.generateRefreshToken();
        log.info("RefreshToken 발급 완료: {}", refreshToken);

        // Redis 저장
        String redisKey = "Refresh:" + TenantContext.getTenant() + userInfo.getId();
        log.info("Redis 저장 시도: key={}, value={}", redisKey, refreshToken);
        redisTemplate.opsForValue().set(redisKey, refreshToken, 7, TimeUnit.DAYS);
        log.info("Redis 저장 완료");

        return new TokenResponse(accessToken, refreshToken, TenantContext.getTenant(), userOfRoles);
    }

    public String generatePassword() {
        Random r = new Random();
        // 100000 ~ 999999
        return String.valueOf(r.nextInt(900000) + 100000);
    }

    @Override
    public void logout(String accessToken) {
        Long userId = jwtUtil.getUserIdFromToken(accessToken);

        // AccessToken 블랙리스트 처리
        long expiration = jwtUtil.getExpiration(accessToken);
        String blacklistKey = "Blacklist:" + accessToken;
        redisTemplate.opsForValue().set(blacklistKey, "logout", expiration, TimeUnit.MILLISECONDS);

        // Redis에 저장된 RefreshToken 제거
        String refreshKey = "Refresh:" + TenantContext.getTenant() + userId;
        redisTemplate.delete(refreshKey);
    }


}
