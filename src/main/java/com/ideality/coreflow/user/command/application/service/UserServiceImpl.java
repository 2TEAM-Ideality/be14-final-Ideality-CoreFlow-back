package com.ideality.coreflow.user.command.application.service;

import com.ideality.coreflow.auth.command.domain.aggregate.LoginType;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.tenant.config.TenantContext;
import com.ideality.coreflow.user.command.application.dto.LoginDTO;
import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;
import com.ideality.coreflow.user.command.domain.aggregate.User;
import com.ideality.coreflow.user.command.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
    public LoginDTO findUserInfoByIdentifier(String identifier, LoginType loginType) {

        log.info("Transactional Propagation.REQUIRES_NEW");
        log.info("userService에요");
        log.info("tenant: {}", TenantContext.getTenant());
        TenantContext.setTenant(TenantContext.getTenant());

        LoginDTO result = new LoginDTO();
        User user;

        if (loginType == LoginType.EMPLOYEE_NUM) {
            user = userRepository.findByEmployeeNum(identifier)
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));
        } else {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));
        }

        if (user.getIsResign()) {
            throw new BaseException(ErrorCode.RESIGNED_USER);
        }
        result.setId(user.getId());
        result.setEmployeeNum(user.getEmployeeNum());
        result.setPassword(user.getPassword());

        return result;
    }

    @Override
    public Boolean isExistEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Long registUser(UserInfoDTO userInfo) {
        User user = User.builder()
                .employeeNum(userInfo.getEmployeeNum())
                .password(userInfo.getPassword())
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .birth(userInfo.getBirth())
                .hireDate(userInfo.getHireDate())
                .isResign(false)
                .deptName(userInfo.getDeptName())
                .jobRankName(userInfo.getJobRankName())
                .jobRoleName(userInfo.getJobRoleName())
                .build();

        return userRepository.save(user).getId();
    }
}
