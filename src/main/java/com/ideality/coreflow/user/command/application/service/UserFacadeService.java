package com.ideality.coreflow.user.command.application.service;

import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacadeService {

    private final UserService userService;

    public void modifyUserProfileImg(UserInfoDTO userInfoDTO) {
        userService.updateUser(userInfoDTO);
    }
}
