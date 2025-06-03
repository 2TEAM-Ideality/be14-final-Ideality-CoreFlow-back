package com.ideality.coreflow.user.command.application.controller;

import com.ideality.coreflow.auth.command.application.dto.UpdatePwdDTO;
import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.user.command.application.dto.RequestUpdateProfile;
import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;
import com.ideality.coreflow.user.command.application.service.UserFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserFacadeService userFacadeService;

    @PatchMapping("/update-profile")
    public ResponseEntity<APIResponse<?>> modifyUserProfileImg(@RequestBody RequestUpdateProfile request) {

        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .id(Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName()))
                .profileImage(request.getProfileImage()).build();
        userFacadeService.modifyUserProfileImg(userInfoDTO);

        return ResponseEntity.ok(APIResponse.success(null, "프로필 사진 변경 완료"));
    }
}
