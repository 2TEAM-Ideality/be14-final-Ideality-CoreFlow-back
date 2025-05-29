package com.ideality.coreflow.auth.command.application.service;

import com.ideality.coreflow.auth.command.application.dto.response.TokenResponse;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.common.tenant.config.TenantContext;
import com.ideality.coreflow.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public TokenResponse login(String userPassword, String password) {

        log.info("로그인 로직 시작");
        // 비밀번호와 user 데이터를 비교해서 로그인

        if (!passwordEncoder.matches(password, userPassword)) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmployeeNum(), TenantContext.getTenant(), roles);

        return null;
    }
}
