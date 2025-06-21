package com.ideality.coreflow.tenant.command.application.service;

import com.ideality.coreflow.tenant.command.application.dto.RequestCreateTenant;

public interface TenantService {

    String findSchemaNameByCompanyCode(String companyCode);

    void createNewTenant(String tenantName);

    Long registTenant(RequestCreateTenant request);
}
