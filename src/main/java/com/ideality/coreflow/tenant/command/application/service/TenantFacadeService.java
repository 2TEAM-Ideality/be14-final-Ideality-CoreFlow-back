package com.ideality.coreflow.tenant.command.application.service;

import com.ideality.coreflow.tenant.command.application.dto.RequestCreateTenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantFacadeService {

    private final TenantService tenantService;

    @Transactional
    public Long createNewTenant(RequestCreateTenant request) {
        // 새 테넌트 생성
        tenantService.createNewTenant(request.getSchemaName());

        // erp_master에 해당 테넌트 정보 등록
        return tenantService.registTenant(request);
    }
}
