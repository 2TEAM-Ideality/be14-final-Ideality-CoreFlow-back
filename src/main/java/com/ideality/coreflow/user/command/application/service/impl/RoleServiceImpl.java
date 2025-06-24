package com.ideality.coreflow.user.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.user.command.application.service.RoleService;
import com.ideality.coreflow.user.command.domain.aggregate.Role;
import com.ideality.coreflow.user.command.domain.aggregate.RoleName;
import com.ideality.coreflow.user.command.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public long findRoleByName(RoleName roleName) {
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));

        return role.getId();
    }
}
