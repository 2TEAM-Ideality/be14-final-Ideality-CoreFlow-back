package com.ideality.coreflow.auth.command.application.service;

import com.ideality.coreflow.auth.command.application.dto.response.TokenResponse;
import com.ideality.coreflow.user.command.application.dto.LoginDTO;

import java.util.List;

public interface AuthService {

    TokenResponse login(LoginDTO userInfo, String password, List<String> userOfRoles);

    String generatePassword();

    void logout(String accessToken);
}
