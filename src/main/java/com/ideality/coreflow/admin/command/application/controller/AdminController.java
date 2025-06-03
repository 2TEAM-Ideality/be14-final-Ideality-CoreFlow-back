package com.ideality.coreflow.admin.command.application.controller;

import com.ideality.coreflow.admin.command.application.dto.RequestUserUpdate;
import com.ideality.coreflow.admin.command.application.service.AdminFacadeService;
import com.ideality.coreflow.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(("/api/admin"))
public class AdminController {

    private final AdminFacadeService adminFacadeService;

    @PostMapping("/users/{userId}")
    public ResponseEntity<APIResponse<?>> updateUserInfo(@PathVariable Long userId, @RequestBody RequestUserUpdate requestUserUpdate) {

        adminFacadeService.updateUserinfo(userId, requestUserUpdate);

        return ResponseEntity.ok(APIResponse.success(null, "유저 정보 수정 완료"));
    }
}
