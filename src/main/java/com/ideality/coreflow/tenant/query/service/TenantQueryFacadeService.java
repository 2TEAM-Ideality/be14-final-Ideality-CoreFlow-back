package com.ideality.coreflow.tenant.query.service;

import com.ideality.coreflow.tenant.query.dto.ResponseSchemaInfo;
import com.ideality.coreflow.tenant.query.mapper.TenantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantQueryFacadeService {

    private final TenantMapper tenantMapper;

    public List<ResponseSchemaInfo> findAllTenant() {
        return tenantMapper.selectAllTenant();
    }
}
