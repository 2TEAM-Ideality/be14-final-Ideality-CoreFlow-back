package com.ideality.coreflow.auth.command.application.controller;

import com.ideality.coreflow.auth.command.application.dto.RequestLogin;
import com.ideality.coreflow.auth.command.application.dto.RequestSignUp;
import com.ideality.coreflow.auth.command.application.dto.RequestSignUpPartner;
import com.ideality.coreflow.auth.command.application.dto.RequestTokenReissue;
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
    public ResponseEntity<APIResponse<?>> loginEntry(@RequestBody RequestLogin requestLogin) {
        return ResponseEntity.ok(APIResponse.success(authFacadeService.login(requestLogin), "로그인 성공"));
    }

    @PostMapping("/signup")
    public ResponseEntity<APIResponse<?>> signupEntry(@RequestBody RequestSignUp requestSignUp) {
        authFacadeService.signUp(requestSignUp);
        return ResponseEntity.ok(APIResponse.success(null, "회원가입 성공"));
    }

    // 협력 업체 계정 생성
    @PostMapping("/signup-partner")
    public ResponseEntity<APIResponse<?>> signupPartner(@RequestBody RequestSignUpPartner request) {
        authFacadeService.signUpPartner(request);
        return ResponseEntity.ok(APIResponse.success(null, "협력업체 계정 생성 완료"));
    }

    @PostMapping("logout")
    public ResponseEntity<APIResponse<?>> logoutEntry(HttpServletRequest request) {
        String accessToken = jwtUtil.extractAccessToken(request);
        authFacadeService.logout(accessToken);
        return ResponseEntity.ok(APIResponse.success(null, "로그아웃 완료"));
    }

    // 재발급
    @PostMapping("/reissue")
    public ResponseEntity<APIResponse<?>> reissueToken(@RequestBody RequestTokenReissue request) {
        String refreshToken = request.getRefreshToken();
        Long userId = request.getUserId();
        return ResponseEntity.ok(APIResponse.success(authFacadeService.reissueAccessToken(refreshToken, userId), "Access Token 재발급 완료"));
    }
}
