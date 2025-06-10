package com.ideality.coreflow.user.command.application.service;

import com.ideality.coreflow.auth.command.domain.aggregate.LoginType;
import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;
import com.ideality.coreflow.user.command.domain.aggregate.OrgType;

import java.util.List;

public interface UserService {

    UserInfoDTO findLoginInfoByIdentifier(String identifier, LoginType loginType);

    Boolean isExistEmail(String email);

    Long registUser(UserInfoDTO userInfo);

    String findEmployeeNumById(Long userId);

    void updateUser(UserInfoDTO updateUserInfo);

    String findPwdById(long id);

    UserInfoDTO findUserByEmployeeNum(String employeeNum);

    long findUserIdByEmail(String email);

    void updateUserOrg(OrgType type, String prevJobRankName, String newJobRankName);

    UserInfoDTO findUserById(long userId);

    void existsUserId(List<Long> leaderUserIds);
}
