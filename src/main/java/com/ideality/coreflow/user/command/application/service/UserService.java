package com.ideality.coreflow.user.command.application.service;

import com.ideality.coreflow.auth.command.domain.aggregate.LoginType;
import com.ideality.coreflow.user.command.application.dto.LoginDTO;
import com.ideality.coreflow.user.command.domain.aggregate.User;

public interface UserService {

    User findUserByIdentifier(String identifier, LoginType loginType);

    LoginDTO findUserInfoByIdentifier(String identifier, LoginType loginType);
}
