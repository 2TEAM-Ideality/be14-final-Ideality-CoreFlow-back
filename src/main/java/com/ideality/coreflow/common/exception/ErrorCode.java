package com.ideality.coreflow.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ✅ 인증(Authentication) 관련
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),          // 401

    // ✅ 인가(Authorization) 관련
    FORBIDDEN("FORBIDDEN", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),                 // 403

    // ✅ 접근 오류 (업무 로직에 따라 거절된 접근)
    ACCESS_DENIED("ACCESS_DENIED", "허용되지 않은 접근입니다.", HttpStatus.FORBIDDEN),    // 403

    // ✅ 서버 오류
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR), // 500

    // ✅ 없는 정보
    TEMPLATE_NOT_FOUND("TEMPLATE_NOT_FOUND", "존재하지 않는 템플릿입니다.", HttpStatus.NOT_FOUND)   // 400
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}