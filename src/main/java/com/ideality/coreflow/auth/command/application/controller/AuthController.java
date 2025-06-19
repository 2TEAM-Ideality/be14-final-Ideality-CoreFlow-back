package com.ideality.coreflow.auth.command.application.controller;

import com.ideality.coreflow.auth.command.application.dto.*;
import com.ideality.coreflow.auth.command.application.service.AuthFacadeService;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.security.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthFacadeService authFacadeService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<APIResponse<?>> loginEntry(@RequestBody RequestLogin requestLogin, HttpServletResponse response) {
        ResponseTokenAndUserInfo res = authFacadeService.login(requestLogin);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", res.getRefreshToken())
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        res.deleteRefreshToken();
        return ResponseEntity.ok(APIResponse.success(res, "로그인 성공"));
    }

    @PostMapping("/signup")
    public ResponseEntity<APIResponse<?>> signupEntry(@RequestBody RequestSignUp requestSignUp) {
        return ResponseEntity.ok(APIResponse.success(authFacadeService.signUp(requestSignUp), "회원가입 성공"));
    }

    // 협력 업체 계정 생성
    @PostMapping("/signup-partner")
    public ResponseEntity<APIResponse<?>> signupPartner(@RequestBody RequestSignUpPartner request) {
        return ResponseEntity.ok(APIResponse.success(authFacadeService.signUpPartner(request), "협력업체 계정 생성 완료"));
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<?>> logoutEntry(HttpServletRequest request, HttpServletResponse response) {
        log.info("로그아웃 요청");
        String accessToken = jwtUtil.extractAccessToken(request);
        authFacadeService.logout(accessToken);
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(0))
                .sameSite("Strict")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        return ResponseEntity.ok(APIResponse.success(null, "로그아웃 완료"));
    }

    // 재발급
    @PostMapping("/reissue")
    public ResponseEntity<APIResponse<?>> reissueToken(@RequestBody RequestTokenReissue request,
                                                       HttpServletRequest httpRequest,
                                                       HttpServletResponse httpResponse) {
//        String refreshToken = request.getRefreshToken();
        String refreshToken = jwtUtil.extractRefreshTokenFromCookie(httpRequest);

        Long userId = request.getUserId();
        ResponseTokenAndUserInfo res = authFacadeService.reissueAccessToken(refreshToken, userId);
        res.deleteRefreshToken();
        return ResponseEntity.ok(APIResponse.success(res, "Access Token 재발급 완료"));
    }

    // 비밀번호 수정
    @PatchMapping("/update-pwd")
    public ResponseEntity<APIResponse<?>> modifyPassword(@RequestBody RequestUpdatePwd request) {

        UpdatePwdDTO updatePwdInfo = UpdatePwdDTO.builder()
                .id(Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName()))
                .prevPassword(request.getPrevPassword())
                .newPassword(request.getNewPassword())
                .build();

        authFacadeService.modifyPassword(updatePwdInfo);

        return ResponseEntity.ok(APIResponse.success(null, "비밀번호 변경 완료"));
    }

    // 비밀번호 찾기 (인증 번호 발송)
    @PostMapping("/reset-password/request")
    public ResponseEntity<APIResponse<?>> resetPasswordRequest(@RequestBody RequestResetPassword request) {
        authFacadeService.resetPasswordRequest(request);
        return ResponseEntity.ok(APIResponse.success(null, "이메일로 인증 코드를 발송했습니다."));
    }

    // 비밀번호 찾기 (인증 번호 검증)
    @PostMapping("/reset-password/verify")
    public ResponseEntity<APIResponse<?>> resetPasswordVerify(@RequestBody RequestResetPasswordVerify request) {
        authFacadeService.resetPasswordVerify(request);
        return ResponseEntity.ok(APIResponse.success(null, "이메일로 새 비밀번호가 발급되었습니다."));
    }
}
