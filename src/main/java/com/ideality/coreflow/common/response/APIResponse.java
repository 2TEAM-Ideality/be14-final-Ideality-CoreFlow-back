package com.ideality.coreflow.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class APIResponse<T> {

    private String status;  // 성공인지, 에러면 무슨 에러 코드인지
    private String message; // 화면에 실제로 띄울 메시지
    private T data; // 조회나 생성 시에, 실제로 들고 올 데이터들

    // 성공 응답 생성
    public static <T> APIResponse<T> success(T data) {
        return new APIResponse<>("success",null, data);
    }

    // 성공 응답(메시지 포함) 생성
    public static <T> APIResponse<T> success(T data, String message) {
        return new APIResponse<>("success", message, data);
    }

    // 실패 응답 생성
    public static <T> APIResponse<T> fail(String message) {
        return new APIResponse<>("fail",  message, null);
    }

    // 에러 응답 생성
    public static <T> APIResponse<T> error(String message) {
        return new APIResponse<>("error", message, null);
    }
}
