package com.ideality.coreflow.auth.command.application.service;

import com.ideality.coreflow.auth.command.application.dto.ResponseToken;
import com.ideality.coreflow.user.command.application.dto.LoginDTO;

import java.time.LocalDate;
import java.util.List;

public interface AuthService {

    ResponseToken login(LoginDTO userInfo, String password, List<String> userOfRoles);

    String generatePassword();

    void logout(String accessToken);

    void validateRefreshToken(String refreshToken, Long userId);

    ResponseToken reissuAccessToken(Long userId, String employeeNum, List<String> userOfRoles);

    String generateEmployeeNum(LocalDate hireDate, Long deptId, long sequence);
}
