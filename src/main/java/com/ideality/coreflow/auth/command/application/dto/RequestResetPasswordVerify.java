package com.ideality.coreflow.auth.command.application.dto;

import lombok.Getter;

@Getter
public class RequestResetPasswordVerify {
    String companyCode;
    String email;
    String verificationCode;
}
