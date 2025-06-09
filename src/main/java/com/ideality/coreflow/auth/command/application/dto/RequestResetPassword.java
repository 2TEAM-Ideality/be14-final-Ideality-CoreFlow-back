package com.ideality.coreflow.auth.command.application.dto;

import lombok.Getter;

@Getter
public class RequestResetPassword {
    String companyCode;
    String name;
    String email;
    String employeeNum;
}
