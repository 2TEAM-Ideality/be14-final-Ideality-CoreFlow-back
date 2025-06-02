package com.ideality.coreflow.auth.command.application.controller;

import com.ideality.coreflow.auth.command.application.dto.request.LoginRequest;
import com.ideality.coreflow.auth.command.application.dto.request.SignUpRequest;
import com.ideality.coreflow.auth.command.application.service.AuthFacadeService;
import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtUtil jwtUtil;

    @PostMapping("login")
    public ResponseEntity<APIResponse<?>> loginEntry(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(APIResponse.success(authFacadeService.login(loginRequest), "로그인 성공"));
    }

    @PostMapping("/signup")
    public ResponseEntity<APIResponse<?>> signupEntry(@RequestBody SignUpRequest signUpRequest) {
        authFacadeService.signUp(signUpRequest);
        return ResponseEntity.ok(APIResponse.success(null, "회원가입 성공"));
    }

    @PostMapping("logout")
    public ResponseEntity<APIResponse<?>> logoutEntry(HttpServletRequest request) {
        String accessToken = jwtUtil.extractAccessToken(request);
        authFacadeService.logout(accessToken);
        return ResponseEntity.ok(APIResponse.success(null, "로그아웃 완료"));
    }
}
