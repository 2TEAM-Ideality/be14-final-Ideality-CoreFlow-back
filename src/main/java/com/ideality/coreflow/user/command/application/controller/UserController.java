package com.ideality.coreflow.user.command.application.controller;

import com.ideality.coreflow.user.command.application.service.UserFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserFacadeService userFacadeService;
}
