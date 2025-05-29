package com.ideality.coreflow.auth.command.application.service;

import com.ideality.coreflow.auth.command.domain.aggregate.LoginType;
import com.ideality.coreflow.auth.command.application.dto.request.LoginRequest;
import com.ideality.coreflow.auth.command.application.dto.response.TokenResponse;
import com.ideality.coreflow.tenant.command.application.service.TenantService;
import com.ideality.coreflow.user.command.application.dto.LoginDTO;
import com.ideality.coreflow.user.command.application.service.UserService;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFacadeService {

    private final TenantService tenantService;
    private final UserService userService;
    private final AuthService authService;
    private final UserQueryService userQueryService;

    public TokenResponse login(LoginRequest loginRequest) {

        log.info("request identifier: {}", loginRequest.getIdentifier());

        LoginType loginType = LoginType.fromIdentifier(loginRequest.getIdentifier());
        log.info("loginType: {}", loginType);

        LoginDTO userInfo = userService.findUserInfoByIdentifier(loginRequest.getIdentifier(), loginType);
        log.info("로그인 유저 정보 조회: {}", userInfo);

        List<String> userOfRoles = userQueryService.findGeneralRolesByUserId(userInfo.getId());
        log.info("해당 유저 역할 정보 조회: {}", userOfRoles);

        return authService.login(userInfo, loginRequest.getPassword(), userOfRoles);
    }
}
