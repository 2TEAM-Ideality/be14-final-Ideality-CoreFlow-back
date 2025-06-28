package com.ideality.coreflow.auth.command.application.service.impl;

import com.ideality.coreflow.auth.command.application.dto.RequestResetPassword;
import com.ideality.coreflow.auth.command.application.dto.RequestResetPasswordVerify;
import com.ideality.coreflow.auth.command.application.dto.ResponseToken;
import com.ideality.coreflow.auth.command.application.dto.UpdatePwdDTO;
import com.ideality.coreflow.auth.command.application.service.AuthService;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.tenant.config.TenantContext;
import com.ideality.coreflow.security.jwt.JwtProvider;
import com.ideality.coreflow.security.jwt.JwtUtil;
import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public ResponseToken login(UserInfoDTO userInfo, String password, List<String> userOfRoles) {

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
        String redisKey = "Refresh:" + userInfo.getId();
        log.info("Redis 저장 시도: key={}, value={}", redisKey, refreshToken);
        redisTemplate.opsForValue().set(redisKey, refreshToken, 7, TimeUnit.DAYS);
        log.info("Redis 저장 완료");

        return new ResponseToken(accessToken, refreshToken, TenantContext.getTenant(), userOfRoles, isTempPassword(password));
    }

    private boolean isTempPassword(String password) {
        return password.matches("^\\d{6}$");
    }

    public String generatePassword() {
        Random r = new Random();
        // 100000 ~ 999999
        return String.valueOf(r.nextInt(900000) + 100000);
    }

    @Override
    public void logout(String accessToken) {
        Long userId = jwtUtil.getUserIdFromToken(accessToken);

        // AccessToken 블랙리스트 처리 (TTL: 현재 시점 기준으로 계산)
        long expirationTime = jwtUtil.getExpiration(accessToken); // 만료 시점 (밀리초)
        log.info("expirationTime: {}", expirationTime);
        long now = System.currentTimeMillis();
        long ttl = expirationTime - now;

        log.info("ttl = {}", ttl);
        // 0 이하인 경우 예외 처리 또는 최소 TTL 설정
        if (ttl > 0) {
            String blacklistKey = "Blacklist:" + accessToken;
            redisTemplate.opsForValue().set(blacklistKey, "logout", ttl, TimeUnit.MILLISECONDS);
        }

        // Redis에 저장된 RefreshToken 제거
        String refreshKey = "Refresh:" + userId;
        redisTemplate.delete(refreshKey);
    }

    @Override
    public void validateRefreshToken(String refreshToken, Long userId) {
        log.info("Refresh token: {}", refreshToken);

        // Redis에 등록된 토큰값 조회
        String redisKey = "Refresh:" + userId;
        log.info("redisKey: {}", redisKey);
        String storedRefreshToken = redisTemplate.opsForValue().get(redisKey);
        log.info("storedRefreshToken: {}", storedRefreshToken);

        if(storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new BaseException(ErrorCode.INVALID_TOKEN);
        }
        log.info("refresh token 유효성 검증 완료");
    }

    @Override
    public ResponseToken reissuAccessToken(Long userId, String employeeNum, List<String> userOfRoles) {

        String newAccessToken = jwtProvider.generateAccessToken(userId, employeeNum, TenantContext.getTenant(), userOfRoles);
        log.info("AccessToken 발급 완료: {}", newAccessToken);
        return new ResponseToken(newAccessToken, null, TenantContext.getTenant(), userOfRoles, false);
    }

    @Override
    public String generateEmployeeNum(LocalDate hireDate, Long deptId, long sequence) {

        // 입사 연월 추출
        String yearMonth = hireDate.format(DateTimeFormatter.ofPattern("yyMM"));

        return String.format("%s%03d%03d", yearMonth, deptId, sequence);
    }

    @Override
    public String generatePartnerNum(LocalDate createDate, Long deptId, long sequence) {
        return String.format("%s%03d%03d", createDate.format(DateTimeFormatter.ofPattern("yyMM")), deptId, sequence);
    }

    @Override
    public void validatePwd(UpdatePwdDTO updatePwdInfo, String prevPassword) {
        log.info(prevPassword);
        if (!passwordEncoder.matches(updatePwdInfo.getPrevPassword(), prevPassword)) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }
    }

    @Override
    public boolean verificationUserInfo(UserInfoDTO userInfo, RequestResetPassword request) {
        // 이름, 이메일 검증
        return userInfo.getName().equals(request.getName()) && userInfo.getEmail().equals(request.getEmail());
    }

    @Override
    public String generateVerificationCode(String email) {

        // 인증 코드 생성
        String verificationCode = generatePassword();

        // 인증 키 생성
        String key = "emailAuth:" + email;

        // 유효 시간 5분
        redisTemplate.opsForValue().set(key, verificationCode, 5, TimeUnit.MINUTES);

        return verificationCode;
    }

    @Override
    public boolean validateCode(RequestResetPasswordVerify request) {

        String key = "emailAuth:" + request.getEmail();

        String storedVerificationCode = redisTemplate.opsForValue().get(key);

        return storedVerificationCode != null && storedVerificationCode.equals(request.getVerificationCode());
    }
}
