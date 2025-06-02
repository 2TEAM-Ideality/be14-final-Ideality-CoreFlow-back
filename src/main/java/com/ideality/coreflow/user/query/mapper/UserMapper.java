package com.ideality.coreflow.user.query.mapper;

import com.ideality.coreflow.user.query.dto.UserOfRoleDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    List<UserOfRoleDTO> selectUserOfGeneralRole(Long userId);

    long countByHireMonthAndDeptName(String yearMonth, String deptName);

    List<Long> selectAllUserByDeptName(String deptName);

    Long selectLeaderByDeptName(String deptName);
}
