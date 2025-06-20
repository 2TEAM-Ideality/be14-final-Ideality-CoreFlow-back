package com.ideality.coreflow.tenant.command.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestCreateTenant {
    String companyCode;
    String companyName;
    String schemaName;
}
