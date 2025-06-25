package com.ideality.coreflow.user.command.application.service;

import com.ideality.coreflow.user.command.domain.aggregate.RoleName;

public interface RoleService {
    long findRoleByName(RoleName roleName);
}
