package com.ideality.coreflow.user.query.controller;

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
public class UserController {

    private final UserQueryService userService;

    // @이름으로 회원 조회 (id, name만 반환)
    @GetMapping("/search/name")
    public List<UserNameIdDto> searchUsersByName(@RequestParam String name) {
        return userService.searchUsersByName(name);
    }


}
