package com.ideality.coreflow.tenant.query.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseSchemaInfo {
    Long id;
    String companyCode;
    String companyName;
    String schemaName;
}
