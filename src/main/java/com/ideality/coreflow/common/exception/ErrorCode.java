package com.ideality.coreflow.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    EQUAL_STATUS("EQUAL_STATUS", "현재 상태와 동일한 요청입니다.", HttpStatus.BAD_REQUEST),
    NOT_PROGRESS_STATUS("NOT_PROGRESS_STATUS", "진행중(PROGRESS) 상태의 프로젝트만 완료(COMPLETED)로 전환할 수 있습니다", HttpStatus.BAD_REQUEST),
    NOT_COMPLETED_TASK("NOT_COMPLETED_TASK", "완료되지 않은 태스크가 존재합니다.", HttpStatus.BAD_REQUEST),
    IMPOSSIBLE_CHANGE_PENDING("IMPOSSIBLE_CHANGE_PENDING", "현재 상태에서는 PENDING 상태로 전환할 수 없습니다.", HttpStatus.BAD_REQUEST),

    // 이미 등록된 이메일
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "이미 등록된 이메일입니다.", HttpStatus.BAD_REQUEST),

    // 퇴사한 직원의 계정 로그인 실패
    RESIGNED_USER("RESIGNED_USER", "퇴사한 직원입니다. 로그인 요청이 실패하였습니다.", HttpStatus.BAD_REQUEST),

    // 로그인 요청 시 바디 읽기 실패
    INVALID_LOGIN_REQUEST("INVALID_LOGIN_REQUEST", "로그인 요청이 실패하였습니다.", HttpStatus.BAD_REQUEST),

    // 비밀번호가 틀림
    INVALID_PASSWORD("INVALID_PASSWORD", "비밀번호가 틀립니다.", HttpStatus.NOT_FOUND),

    // 유효하지 않은 토큰
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),

    // 해당 정보 없음
    NOT_FOUND("NOT_FOUND", "해당 정보가 없습니다.", HttpStatus.NOT_FOUND), // 404

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

    // 없는 유저
    USER_NOT_FOUND("USER_NOT_FOUND", "유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // ✅ 선행 작업과 후행 작업 실패
    PREDECESSOR_NOT_FOUND("PREDECESSOR_NOT_FOUND", "선행 작업이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    SUCCESSOR_NOT_FOUND("SUCCESSOR_NOT_FOUND", "후행 작업이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    PREDECESSOR_AND_SUCCESSOR_REQUIRED("PREDECESSOR_AND_SUCCESSOR_REQUIRED", "선행 작업과 후행 작업이 모두 필요합니다.", HttpStatus.BAD_REQUEST),

    // ✅ 없는 부서
    DEPT_NOT_FOUND("DEPT_NOT_FOUND", "부서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // ✅ 추가된 에러 코드: 선행 작업을 찾을 수 없는 경우
    PARENT_TASK_NOT_FOUND("PARENT_TASK_NOT_FOUND", "선행 작업을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    RESOURCE_NOT_FOUND("DATABASE_RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    // ✅ 첨부파일 없는 정보
    ATTCHMENT_NOT_FOUND("ATTACHMENT_NOT_FOUND", "해당 타겟에 대한 첨부파일이 존재하지 않습니다." , HttpStatus.NOT_FOUND),
    DUPLICATED_TARGET_ID("DUPLICATED_TARGET_ID", "해당 타겟에 대한 첨부파일이 이미 존재합니다.", HttpStatus.CONFLICT),

    PROJECT_NOT_FOUND("PROJECT_NOT_FOUND", "존재하지 않는 프로젝트입니다.", HttpStatus.NOT_FOUND),
    TASK_NOT_FOUND("TASK_NOT_FOUND", "존재하는 않는 테스크입니다.", HttpStatus.NOT_FOUND),
    WORK_NOT_FOUND("WORK_NOT_FOUND", "존재하지 않는 태스크 또는 세부일정입니다.", HttpStatus.NOT_FOUND),
    DEPARTMENT_NOT_FOUND("DEPARTMENT_NOT_FOUND", "존재하지 않는 부서입니다.", HttpStatus.NOT_FOUND),
    PARTICIPANT_NOT_FOUND("PARTICIPANT_NOT_FOUND", "참여자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PROJECT_NOT_COMPLETED("PROJECT_NOT_COMPLETED", "완료되지 않은 프로젝트입니다.", HttpStatus.CONFLICT ),
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "존재하지 않는 댓글입니다.", HttpStatus.NOT_FOUND),
    DETAIL_NOT_FOUND("DETAIL_NOT_FOUND", "존재하지 않는 세부일정입니다.", HttpStatus.NOT_FOUND),
    // ✅ 상태 전이 오류
    INVALID_STATUS_PROGRESS("INVALID_STATUS_PROGRESS", "이미 시작된 작업입니다./ progress가 100퍼센트 미만입니다.", HttpStatus.CONFLICT),
    INVALID_STATUS_COMPLETED("INVALID_STATUS_COMPLETED", "이미 완료 처리된 작업입니다.", HttpStatus.CONFLICT),
    INVALID_STATUS_DELETED("INVALID_STATUS_DELETED", "이미 삭제된 작업입니다.", HttpStatus.CONFLICT),

    // 잘못된 json 요청 양식
    INVALID_JSON_FORMAT("INVALID_JSON_FORMAT", "잘못된 json 양식", HttpStatus.BAD_REQUEST),

    // 잘못된 tenant 설정
    INVALID_TENANT("INVALID_TENANT", "Tenant 설정 오류", HttpStatus.BAD_REQUEST),

    // 잘못된 유저 정보
    INVALID_USER_INFO("INVALID_USER_INFO", "유저 이름 또는 이메일이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

    // 잘못 입력한 인증 코드
    INVALID_VERIFICATION_CODE("INVALID_VERIFICATION_CODE", "인증 시간이 만료되었거나 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

    INVALID_SOURCE_LIST("INVALID_SOURCE_LIST", "source는 null이거나 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_TARGET_LIST("INVALID_TARGET_LIST", "target은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST),

    // 이미 처리된 결재 혹은 결재자가 아님
    ACCESS_DENIED_APPROVAL("ACCESS_DENIED_APPROVAL", "결재가 이미 처리되었거나 해당 결재에 권한이 없습니다.", HttpStatus.BAD_REQUEST),

    TASK_PROGRESS_NOT_COMPLETED("TASK_PROGRESS_NOT_COMPLETED","진행률이 100%여야 작업을 완료할 수 있습니다.", HttpStatus.BAD_REQUEST),
    SCHEDULE_NOT_REPEATABLE("SCHEDULE_NOT_REPEATABLE", "반복규칙을 만들 수 없는 일정입니다.", HttpStatus.CONFLICT),

    SCHEDULE_NOT_FOUND("SCHEDULE_NOT_FOUND", "존재하지 않는 일정입니다.", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND("NOTIFICATION_NOT_FOUND", "존재하지 않는 알림입니다.", HttpStatus.NOT_FOUND),
    COMMENT_ALREADY_DELETED("COMMENT_ALREADY_DELETED", "이미 삭제된 댓글입니다." , HttpStatus.CONFLICT),
    REPEATRULE_NOT_FOUND("REPEATRULE_NOT_FOUND", "존재하지 않는 반복규칙입니다.", HttpStatus.NOT_FOUND ),
    NOT_FOUND_NOTIFICATION("NOT_FOUND_NOTIFICATION","알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN_NOTIFICATION("FORBIDDEN_NOTIFICATION","본인의 알림만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN),
    TEAM_LEADER_ALREADY_EXISTS("TEAM_LEADER_ALREADY_EXISTS", "이미 팀장이 존재합니다", HttpStatus.CONFLICT),
    TEAM_MEMBER_ALREADY_EXISTS("TEAM_MEMBER_ALREADY_EXISTS", "이미 팀원으로 참여중인 회원입니다.", HttpStatus.CONFLICT),
	PDF_CREATE_FAILED("PDF_CREATE_FAILED", "프로젝트 리포트 PDF 파일 생성이 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_COMPLETED_PROJECT("NO_COMPLETED_PROJECT", "완료된 프로젝트가 없습니다." , HttpStatus.NOT_FOUND ),
    NOT_FOUND_REFRESH_TOKEN("NOT_FOUND_REFRESH_TOKEN", "리프레시 토큰을 찾지 못하였습니다.", HttpStatus.NOT_FOUND),
    ILLEGAL_TENANT_NAME("ILLEGAL_TENANT_NAME", "허용되지 않은 테넌트 이름입니다.", HttpStatus.BAD_REQUEST),
    TENANT_CREATE_FAILED("TENANT_CREATE_FAILED", "테넌트 DB 생성에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATED_COMPANY_CODE("DUPLICATED_COMPANY_CODE", "이미 존재하는 회사 코드입니다.", HttpStatus.BAD_REQUEST),
    COMMENT_ALREADY_NOTICE("COMMENT_ALREADY_NOTICE", "이미 공지인 댓글입니다." , HttpStatus.CONFLICT),
    INVALID_STATUS_PENDING("INVALID_STATUS_PENDING", "진행중인 프로젝트가 아닙니다." , HttpStatus.BAD_REQUEST),
    ACCESS_DENIED_TEAMLEADER("ACCESS_DENIED_TEAMLEADER", "팀 리더 이상만 이용할 수 있는 권한입니다.",  HttpStatus.FORBIDDEN),
    ACCESS_DENIED_PROJECT("ACCESS_DENIED_PROJECT", "프로젝트 참여자가 이용할 수 있는 권한입니다.", HttpStatus.FORBIDDEN),
    NOT_COMMENT_WRITER("NOT_COMMENT_WRITER", "댓글 작성자가 아닙니다.", HttpStatus.FORBIDDEN),
    INVALID_DURATION("INVALID_DURATION", "종료일이 시작일을 앞설 수 없습니다.", HttpStatus.BAD_REQUEST),
    STATUS_IS_COMPLETED("STATUS_IS_COMPLETED", "완료된 일정입니다.", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED_DIRECTOR("ACCESS_DENIED_DIRECTOR", "디렉터만 이용할 수 있는 권한입니다.",  HttpStatus.FORBIDDEN),
    ACCESS_DENIED_DELETED_PARTICIPANT("ACCESS_DENIED_DELETED_TEAMLEADER", "삭제할 권한이 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_CANCELLED_TASK("NOT_CANCELLED_TASK", "진행 중인 태스크를 취소할 수 있습니다.", HttpStatus.BAD_REQUEST),
    NOT_SOFT_DELETED_TASK("NOT_SOFT_DELETED_TASK", "삭제 처리중인 태스크야 복원할 수 있습니다.", HttpStatus.BAD_REQUEST),
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