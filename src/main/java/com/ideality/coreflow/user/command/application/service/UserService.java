package com.ideality.coreflow.user.command.application.service;

import com.ideality.coreflow.auth.command.domain.aggregate.LoginType;
import com.ideality.coreflow.user.command.application.dto.LoginDTO;
import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;

public interface UserService {

    LoginDTO findLoginInfoByIdentifier(String identifier, LoginType loginType);

    Boolean isExistEmail(String email);

    Long registUser(UserInfoDTO userInfo);

    String findEmployeeNumById(Long userId);

    void updateUser(Long userId, UserInfoDTO updateUserInfo);

    UserInfoDTO findPwdByEmployeeNum(String employeeNum);
}
