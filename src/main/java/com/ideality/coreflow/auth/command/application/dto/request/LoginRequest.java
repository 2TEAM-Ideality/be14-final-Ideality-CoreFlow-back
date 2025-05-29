package com.ideality.coreflow.auth.command.application.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {
    private String identifier;
    private String password;
    private String companyCode;
}
