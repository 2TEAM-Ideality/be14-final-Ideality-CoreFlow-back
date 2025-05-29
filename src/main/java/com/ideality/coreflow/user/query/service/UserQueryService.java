package com.ideality.coreflow.user.query.service;

import com.ideality.coreflow.user.query.dto.UserOfRoleDTO;

import java.util.List;

public interface UserQueryService {

    List<UserOfRoleDTO> findRolesByUserId(Long userId);
}
