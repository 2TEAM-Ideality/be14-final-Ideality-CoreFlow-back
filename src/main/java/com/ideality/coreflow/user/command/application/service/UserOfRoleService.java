package com.ideality.coreflow.user.command.application.service;

import java.util.List;

public interface UserOfRoleService {

    void updateAuthorities(boolean isCreation, long userId, long roleId);
}
