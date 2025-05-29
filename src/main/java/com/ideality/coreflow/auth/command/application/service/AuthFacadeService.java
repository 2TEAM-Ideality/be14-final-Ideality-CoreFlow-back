package com.ideality.coreflow.auth.command.application.service;

import com.ideality.coreflow.auth.command.domain.aggregate.LoginType;
import com.ideality.coreflow.auth.command.application.dto.request.LoginRequest;
import com.ideality.coreflow.auth.command.application.dto.response.TokenResponse;
import com.ideality.coreflow.common.tenant.config.TenantContext;
import com.ideality.coreflow.tenant.command.application.service.TenantService;
import com.ideality.coreflow.user.command.application.service.UserService;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFacadeService {

    private final TenantService tenantService;
    private final UserService userService;
    private final AuthService authService;
    private final UserQueryService userQueryService;

    public TokenResponse login(LoginRequest loginRequest) {

        String schema = tenantService.findSchemaNameByCompanyCode(loginRequest.getCompanyCode());

        log.info("테넌트 세팅 schema: {}", schema);
        TenantContext.setTenant(schema);

        log.info("테넌트 설정 완료: {}", TenantContext.getTenant());

        log.info("request identifier: {}", loginRequest.getIdentifier());

        LoginType loginType = LoginType.fromIdentifier(loginRequest.getIdentifier());
        log.info("loginType: {}", loginType);

        String password = userService.findPwdByIdentifier(loginRequest.getIdentifier(), loginType);
        log.info("유저 비밀번호 조회: {}", password);



        return authService.login(password, loginRequest.getPassword());
    }
}
