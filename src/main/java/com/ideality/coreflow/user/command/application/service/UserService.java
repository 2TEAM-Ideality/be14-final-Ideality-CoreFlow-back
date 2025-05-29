package com.ideality.coreflow.user.command.application.service;

import com.ideality.coreflow.auth.command.domain.aggregate.LoginType;
import com.ideality.coreflow.user.command.domain.aggregate.User;

import java.util.List;

public interface UserService {

    User findUserByIdentifier(String identifier, LoginType loginType);

    String findPwdByIdentifier(String identifier, LoginType loginType);
}
