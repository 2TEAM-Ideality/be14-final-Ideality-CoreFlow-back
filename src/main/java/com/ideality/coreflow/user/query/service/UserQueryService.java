package com.ideality.coreflow.user.query.service;

import com.ideality.coreflow.user.query.dto.UserNameIdDto;

import java.util.List;

public interface UserQueryService {
    List<Long> selectAllUserByDeptName(String deptName);

    List<UserNameIdDto> searchUsersByName(String name);

    List<Long> selectLeadersByDeptName(String deptName);

    String getDeptNameByUserId(Long userId);

    Boolean selectUserById(Long userId);
}
