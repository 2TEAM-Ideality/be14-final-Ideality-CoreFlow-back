package com.ideality.coreflow.user.command.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacadeService {

    private final UserService userService;
}
