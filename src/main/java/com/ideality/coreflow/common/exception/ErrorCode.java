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

    // ✅ 템플릿 없는 정보
    TEMPLATE_NOT_FOUND("TEMPLATE_NOT_FOUND", "존재하지 않는 템플릿입니다.", HttpStatus.NOT_FOUND),   // 400

    // ✅ DB 관련
    DATABASE_ERROR("DATABASE_ERROR", "DB 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // ✅ S3 업로드 실패
    S3_UPLOAD_FAILED("S3_UPLOAD_FAILED", "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // ✅ JSON 직렬화 실패
    JSON_SERIALIZATION_ERROR("JSON_SERIALIZATION_ERROR", "JSON 변환에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    RESOURCE_NOT_FOUND("DATABASE_RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    // ✅ 첨부파일 없는 정보
    ATTCHMENT_NOT_FOUND("ATTACHMENT_NOT_FOUND", "해당 첨부파일이 존재하지 않습니다." , HttpStatus.NOT_FOUND),

    PROJECT_NOT_FOUND("PROJECT_NOT_FOUND", "존재하지 않는 프로젝트입니다.", HttpStatus.NOT_FOUND),
    TASK_NOT_FOUND("TASK_NOT_FOUND", "존재하는 않는 테스크입니다.", HttpStatus.NOT_FOUND),
    DEPARTMENT_NOT_FOUND("DEPARTMENT_NOT_FOUND", "존재하지 않는 부서입니다.", HttpStatus.NOT_FOUND),
    PARTICIPANT_NOT_FOUND("PARTICIPANT_NOT_FOUND", "참여자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    // ✅ 상태 전이 오류
    INVALID_STATUS_PROGRESS("INVALID_STATUS_PROGRESS", "이미 시작된 작업입니다.", HttpStatus.CONFLICT),
    INVALID_STATUS_COMPLETED("INVALID_STATUS_COMPLETED", "이미 완료 처리된 작업입니다.", HttpStatus.CONFLICT),
    INVALID_STATUS_DELETED("INVALID_STATUS_DELETED", "이미 삭제된 작업입니다.", HttpStatus.CONFLICT),

    INVALID_SOURCE_LIST("INVALID_SOURCE_LIST", "source는 null이거나 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_TARGET_LIST("INVALID_TARGET_LIST", "target은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST)
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