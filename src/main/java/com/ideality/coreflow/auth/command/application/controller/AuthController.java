package com.ideality.coreflow.auth.command.application.controller;

import com.ideality.coreflow.auth.command.application.dto.request.LoginRequest;
import com.ideality.coreflow.auth.command.application.dto.request.SignUpRequest;
import com.ideality.coreflow.auth.command.application.service.AuthFacadeService;
import com.ideality.coreflow.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthFacadeService authFacadeService;

    @PostMapping("login")
    public ResponseEntity<APIResponse<?>> loginEntry(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(APIResponse.success(authFacadeService.login(loginRequest), "로그인 성공"));
    }

    @PostMapping("/signup")
    public ResponseEntity<APIResponse<?>> signupEntry(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(APIResponse.success(null, "회원가입 성공"));
    }
}
