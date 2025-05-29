package com.ideality.coreflow.user.command.application.service;

import com.ideality.coreflow.auth.command.domain.aggregate.LoginType;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.user.command.domain.aggregate.User;
import com.ideality.coreflow.user.command.domain.aggregate.UserOfRole;
import com.ideality.coreflow.user.command.domain.repository.UserOfRoleRepository;
import com.ideality.coreflow.user.command.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserOfRoleRepository userOfRoleRepository;

    @Override
    public User findUserByIdentifier(String identifier, LoginType loginType) {

        if (loginType == LoginType.EMPLOYEE_NUM) {
            return userRepository.findByEmployeeNum(identifier)
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));
        } else {
            return userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));
        }
    }

    @Override
    public String findPwdByIdentifier(String identifier, LoginType loginType) {

        if (loginType == LoginType.EMPLOYEE_NUM) {
            return userRepository.findByEmployeeNum(identifier)
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND)).getPassword();
        } else {
            return userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND)).getPassword();
        }
    }
}
