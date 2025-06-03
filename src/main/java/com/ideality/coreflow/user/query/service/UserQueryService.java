package com.ideality.coreflow.user.query.service;

import com.ideality.coreflow.user.query.dto.DeptNameAndYearDTO;

import java.util.List;

public interface UserQueryService {

    List<String> findGeneralRolesByUserId(Long userId);

    Long countByHireMonthAndDeptName(DeptNameAndYearDTO countByDeptNameAndYearDTO);

    List<Long> selectAllUserByDeptName(String deptName);

    Long selectLeaderByDeptName(String deptName);
}
