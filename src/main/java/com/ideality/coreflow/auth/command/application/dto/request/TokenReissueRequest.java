package com.ideality.coreflow.auth.command.application.dto.request;

import lombok.Getter;

@Getter
public class TokenReissueRequest {
    private String refreshToken;
    private Long userId;
    private String companySchema;
}
