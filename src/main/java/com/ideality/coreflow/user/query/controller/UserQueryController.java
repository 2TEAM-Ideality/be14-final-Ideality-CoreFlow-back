package com.ideality.coreflow.user.query.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.user.query.dto.UserNameIdDto;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserQueryController {

    private final UserQueryService userService;

    // @이름으로 회원 조회 (id, name만 반환)
    @GetMapping("/name")
    public APIResponse<List<UserNameIdDto>> searchUsersByName(@RequestParam String name) {
        List<UserNameIdDto> users = userService.searchUsersByName(name);
        return APIResponse.success(users,"@user이름으로 조회 완료");  // 성공 응답 생성
    }

    // 구성원 목록 조회용
    @GetMapping("/find-all")
    public APIResponse<?> findAllUsers() {
        return APIResponse.success(userService.findAllUsers(), "유저 목록 조회");
    }
}
