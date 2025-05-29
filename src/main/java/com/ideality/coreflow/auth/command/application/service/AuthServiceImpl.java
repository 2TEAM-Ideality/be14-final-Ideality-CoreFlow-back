package com.ideality.coreflow.auth.command.application.service;

import com.ideality.coreflow.auth.command.application.dto.response.TokenResponse;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.tenant.config.TenantContext;
import com.ideality.coreflow.security.jwt.JwtProvider;
import com.ideality.coreflow.user.command.application.dto.LoginDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public TokenResponse login(LoginDTO userInfo, String password, List<String> userOfRoles) {

        log.info("로그인 로직 시작");

        // 비밀번호 비교
        if (!passwordEncoder.matches(password, userInfo.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        log.info("비밀번호 매칭 완료, 토큰 생성 시작");

        // accessToken 생성
        String accessToken = jwtProvider.generateAccessToken(userInfo.getId(), userInfo.getEmployeeNum(), TenantContext.getTenant(), userOfRoles);

        // refreshToken 생성할 곳

        // Redis 저장할 곳

        return new TokenResponse(accessToken, null, TenantContext.getTenant(), userOfRoles);
    }
}
