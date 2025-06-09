package com.ideality.coreflow.auth.command.application.service;

import com.ideality.coreflow.auth.command.application.dto.RequestResetPassword;
import com.ideality.coreflow.auth.command.application.dto.RequestResetPasswordVerify;
import com.ideality.coreflow.auth.command.application.dto.ResponseToken;
import com.ideality.coreflow.auth.command.application.dto.UpdatePwdDTO;
import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;

import java.time.LocalDate;
import java.util.List;

public interface AuthService {

    ResponseToken login(UserInfoDTO userInfo, String password, List<String> userOfRoles);

    String generatePassword();

    void logout(String accessToken);

    void validateRefreshToken(String refreshToken, Long userId);

    ResponseToken reissuAccessToken(Long userId, String employeeNum, List<String> userOfRoles);

    String generateEmployeeNum(LocalDate hireDate, Long deptId, long sequence);

    String generatePartnerNum(LocalDate createDate, Long deptId, long sequence);

    void validatePwd(UpdatePwdDTO updatePwdInfo, String prevPassword);

    boolean verificationUserInfo(UserInfoDTO userInfo, RequestResetPassword request);

    String generateVerificationCode(String email);

    boolean validateCode(RequestResetPasswordVerify request);
}
