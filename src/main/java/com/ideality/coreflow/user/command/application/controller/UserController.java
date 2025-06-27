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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserFacadeService userFacadeService;

    @PatchMapping("/update-profile")
    public ResponseEntity<APIResponse<?>> modifyUserProfileImg(@ModelAttribute RequestUpdateProfile request) {
        String url = userFacadeService.modifyUserProfileImg(request);

        return ResponseEntity.ok(APIResponse.success(Map.of("profileImage", url), "프로필 사진 변경 완료"));
    }

    @DeleteMapping("/delete-profile/{userId}")
    public ResponseEntity<APIResponse<?>> deleteUserProfileImg(@PathVariable Long userId) {
        userFacadeService.deleteUserProfileImg(userId);
        return ResponseEntity.ok(APIResponse.success(null, "프로필 사진 삭제 완료"));
    }

    // 현재 로그인한 유저의 회원 정보
    @GetMapping("/info")
    public ResponseEntity<APIResponse<?>> findUserInfo() {

        return ResponseEntity.ok(APIResponse.success(userFacadeService.findUserInfo(Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName())), "현재 회원 정보 조회"));
    }

    // 회원 id로 유저 정보 조회
    @GetMapping("/info/{userId}")
    public ResponseEntity<APIResponse<?>> findUserInfoByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(APIResponse.success(userFacadeService.findUserInfo(userId), "유저 정보 조회"));
    }
}
