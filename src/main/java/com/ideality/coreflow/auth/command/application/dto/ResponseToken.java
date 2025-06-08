package com.ideality.coreflow.auth.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ResponseToken {
    String accessToken;
    String refreshToken;
    String schemaName;
    List<String> roles;
    boolean isTemp;
}
