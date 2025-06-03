package com.ideality.coreflow.user.query.service;

import com.ideality.coreflow.user.query.dto.DeptNameAndMonthDTO;

import java.util.List;

public interface UserQueryService {

    List<String> findGeneralRolesByUserId(Long userId);

    Long countByHireMonthAndDeptName(DeptNameAndMonthDTO countByDeptNameAndMonthDTO);

    List<Long> selectAllUserByDeptName(String deptName);

    Long selectLeaderByDeptName(String deptName);
}
