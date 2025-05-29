package com.ideality.coreflow.tenant.command.application.service;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.common.tenant.config.TenantDataSourceProvider;
import com.ideality.coreflow.tenant.command.domain.aggregate.ErpMaster;
import com.ideality.coreflow.tenant.command.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantDataSourceProvider tenantDataSourceProvider;

    @Override
    public String findSchemaNameByCompanyCode(String companyCode) {

        ErpMaster tenantInfo = tenantRepository.findByCompanyCode(companyCode)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));

        log.info("tenantInfo={}", tenantInfo);

        return tenantInfo.getCompanySchema();
    }
}
