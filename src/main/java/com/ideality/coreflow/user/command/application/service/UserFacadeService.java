package com.ideality.coreflow.user.command.application.service;

import com.ideality.coreflow.user.command.application.dto.ResponseUserInfo;
import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFacadeService {

    private final UserService userService;
    private final UserQueryService userQueryService;

    @Transactional
    public void modifyUserProfileImg(UserInfoDTO userInfoDTO) {
        userService.updateUser(userInfoDTO);
    }

    @Transactional
    public ResponseUserInfo findUserInfo(long userId) {

        UserInfoDTO userInfo = userService.findUserById(userId);

        List<String> userOfRoles = userQueryService.findGeneralRolesByUserId(userId);

        Boolean isCreation = userOfRoles.contains("CREATOR");

        return ResponseUserInfo.builder()
                .id(userInfo.getId())
                .employeeNum(userInfo.getEmployeeNum())
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .birth(userInfo.getBirth())
                .hireDate(userInfo.getHireDate())
                .isResign(userInfo.getIsResign())
                .resignDate(userInfo.getResignDate())
                .profileImage(userInfo.getProfileImage())
                .deptName(userInfo.getDeptName())
                .jobRankName(userInfo.getJobRankName())
                .jobRoleName(userInfo.getJobRoleName())
                .isCreation(isCreation)
                .roles(userOfRoles)
                .build();
    }

    public void deleteUserProfileImg(long userId) {
        userService.deleteUserProfileImg(userId);
    }
}
