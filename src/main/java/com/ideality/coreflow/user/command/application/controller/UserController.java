package com.ideality.coreflow.user.command.application.controller;

import com.ideality.coreflow.common.response.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/user")
public class UserController {

    // 본인 회원정보 수정
    @PatchMapping("/update")
    public ResponseEntity<APIResponse<?>> userUpdateUser(@RequestBody RequestUserUpdate request) {

    }
}
