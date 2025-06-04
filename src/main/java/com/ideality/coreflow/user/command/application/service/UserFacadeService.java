package com.ideality.coreflow.user.command.application.service;

import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacadeService {

    private final UserService userService;

    @Transactional
    public void modifyUserProfileImg(UserInfoDTO userInfoDTO) {
        userService.updateUser(userInfoDTO);
    }
}
