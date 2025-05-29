package com.ideality.coreflow.auth.command.application.service;

import com.ideality.coreflow.auth.command.domain.aggregate.LoginType;
import com.ideality.coreflow.auth.command.application.dto.response.TokenResponse;
import com.ideality.coreflow.user.command.domain.aggregate.User;

public interface AuthService {

    TokenResponse login(String userPassword, String password);
}
