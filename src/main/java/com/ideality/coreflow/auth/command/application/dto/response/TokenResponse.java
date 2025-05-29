package com.ideality.coreflow.auth.command.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenResponse {
    String accessToken;
    String refreshToken;
}
