package com.ideality.coreflow.auth.command.application.dto;

import lombok.Getter;

@Getter
public class RequestLogin {
    private String identifier;
    private String password;
    private String companyCode;
}
