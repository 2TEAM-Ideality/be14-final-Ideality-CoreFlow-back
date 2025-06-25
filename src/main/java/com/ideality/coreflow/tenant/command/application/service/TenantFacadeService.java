package com.ideality.coreflow.tenant.command.application.service;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.tenant.config.TenantContext;
import com.ideality.coreflow.tenant.command.application.dto.RequestCreateTenant;
import com.ideality.coreflow.tenant.command.application.dto.ResponseInitialAdmin;
import com.ideality.coreflow.user.command.application.service.RoleService;
import com.ideality.coreflow.user.command.application.service.UserOfRoleService;
import com.ideality.coreflow.user.command.application.service.UserService;
import com.ideality.coreflow.user.command.domain.aggregate.RoleName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantFacadeService {

    private final TenantService tenantService;
    private final UserService userService;
    private final RoleService roleService;
    private final UserOfRoleService userOfRoleService;

    public Long createNewTenant(RequestCreateTenant request) {

        String schemaName = request.getSchemaName();
        Long schemaId = null;

        // 1. erp_master에 해당 테넌트 정보 등록
        try {
            schemaId = tenantService.registTenant(request);
        } catch (Exception e) {
            log.error("테넌트 정보 등록 실패");
            rollbackTenantCreation(schemaName, schemaId);
            throw new BaseException(ErrorCode.TENANT_CREATE_FAILED);
        }

        // 2. 새 테넌트 생성
        try {
            log.info("테넌트 생성 시도");
            tenantService.createNewTenant(schemaName);
            log.info("테넌트 생성 완료");
        } catch (Exception e) {
            log.error("테넌트 생성 실패 롤백");
            rollbackSchemaCreation(schemaName);
            throw new BaseException(ErrorCode.TENANT_CREATE_FAILED);
        }

        // 3. 최초 관리자 계정 생성
        Long finalSchemaId = schemaId;
        new Thread(() -> handleInitialAdminCreation(schemaName, finalSchemaId)).start();

        return schemaId;
    }

    // 관리자 생성 시도
    private void handleInitialAdminCreation(String schemaName, Long schemaId) {
        try {
            TenantContext.setTenant(schemaName);
            log.info("테넌트 컨텍스트 설정: {}", schemaName);

            ResponseInitialAdmin initialAdmin = userService.createInitialAdmin(schemaName);
            long roleId = roleService.findRoleByName(RoleName.ADMIN);
            userOfRoleService.updateAuthorities(true, initialAdmin.getId(), roleId);

            log.info("초기 관리자 계정 생성 완료: {}", initialAdmin.getEmployeeNum());
        } catch (Exception e) {
            log.error("초기 관리자 생성 실패 -> 롤백 시작: {}", schemaName, e);
            rollbackTenantCreation(schemaName, schemaId);
        } finally {
            TenantContext.clear();
        }
    }

    // 등록 정보 삭제
    private void rollbackTenantCreation(String schemaName, Long schemaId) {
        new Thread(() -> {
            try {
                TenantContext.setTenant("master");

                if (schemaId != null) {
                    try {
                        tenantService.deleteTenantById(schemaId);
                        log.info("master DB 롤백 완료: schemaId={}", schemaId);
                    } catch (Exception cleanupError) {
                        log.error("master 롤백 실패: {}", cleanupError.getMessage(), cleanupError);
                    }
                }
            } finally {
                TenantContext.clear();
            }
        }).start();
    }

    // 스키마 삭제
    private void rollbackSchemaCreation(String schemaName) {
        try {
            tenantService.dropTenantSchema(schemaName);
            log.info("테넌트 스키마 삭제 완료: {}", schemaName);
        } catch (Exception schemaDropError) {
            log.error("스키마 삭제 실패: {}", schemaDropError.getMessage(), schemaDropError);
        }
    }

    public void deleteTenant(Long tenantId) {
        String tenantName = tenantService.findSchemaNameById(tenantId);
        tenantService.deleteTenantById(tenantId);
        rollbackSchemaCreation(tenantName);
    }
}
