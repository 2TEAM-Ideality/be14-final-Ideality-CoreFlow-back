package com.ideality.coreflow.common.exception;

import com.ideality.coreflow.common.response.APIResponse;
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

    /* 설명. 예상치 못한 일반 예외 처리 -> 500번대 서버 에러 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Void>> handleException(Exception ex) {
        ex.printStackTrace(); // or log.error(...)
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}