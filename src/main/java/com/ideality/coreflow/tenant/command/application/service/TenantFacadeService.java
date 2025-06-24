package com.ideality.coreflow.tenant.command.application.service;

import com.ideality.coreflow.infra.tenant.config.TenantContext;
import com.ideality.coreflow.tenant.command.application.dto.RequestCreateTenant;
import com.ideality.coreflow.tenant.command.application.dto.ResponseInitialAdmin;
import com.ideality.coreflow.user.command.application.service.RoleService;
import com.ideality.coreflow.user.command.application.service.UserOfRoleService;
import com.ideality.coreflow.user.command.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantFacadeService {

    private final TenantService tenantService;
    private final UserService userService;
    private final RoleService roleService;
    private final UserOfRoleService userOfRoleService;

    @Transactional
    public Long createNewTenant(RequestCreateTenant request) {

        String schemaName = request.getSchemaName();

        // erp_master에 해당 테넌트 정보 등록
        Long schemaId = tenantService.registTenant(request);

        // 새 테넌트 생성
        tenantService.createNewTenant(schemaName);

        // 최초 관리자 계정 생성
        new Thread(() -> {
            TenantContext.setTenant(schemaName);

            ResponseInitialAdmin initialAdmin = userService.createInitialAdmin(schemaName);

            long roleId = roleService.findRoleByName("ADMIN");

            userOfRoleService.updateAuthorities(true, initialAdmin.getId(), roleId);

        }).start();

        return schemaId;
    }
}
