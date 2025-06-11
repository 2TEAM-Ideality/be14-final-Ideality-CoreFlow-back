package com.ideality.coreflow.common.exception;

import com.ideality.coreflow.common.response.APIResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* 설명. 커스텀 비즈니스 예외 -> ErrorCode에서 만든 예외 */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<APIResponse<Void>> handleBaseException(BaseException ex) {
        ErrorCode code = ex.getErrorCode();
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(APIResponse.fail(code.getMessage()));
    }

    // ✅ 입력값 유효성 검사 실패 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .badRequest()
                .body(APIResponse.fail(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(HttpServletRequest request, Exception ex) {
        ex.printStackTrace(); // 또는 log.error("Unhandled exception", ex);

        // 1. SSE 요청은 JSON 반환 불가 → 204 No Content 등 간단한 응답
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("text/event-stream")) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // 2. 일반 REST 요청 → APIResponse 래핑해서 500 반환
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }

}