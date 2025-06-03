package com.ideality.coreflow.auth.command.application.dto;

import lombok.Getter;

@Getter
public class RequestTokenReissue {
    private String refreshToken;
    private Long userId;
    private String companySchema;
}
