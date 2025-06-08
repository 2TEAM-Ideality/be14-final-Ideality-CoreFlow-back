package com.ideality.coreflow.user.command.application.service;

public interface UserOfRoleService {

    void updateAuthorities(boolean isCreation, long userId, long roleId);
}
