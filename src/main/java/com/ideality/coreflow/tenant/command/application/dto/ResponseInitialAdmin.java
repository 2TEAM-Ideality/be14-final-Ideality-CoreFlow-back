package com.ideality.coreflow.tenant.command.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseInitialAdmin {
    long id;
    String employeeNum;
    String password;
    String schemaName;
}
