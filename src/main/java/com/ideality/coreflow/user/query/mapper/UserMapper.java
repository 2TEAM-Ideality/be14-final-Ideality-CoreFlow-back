package com.ideality.coreflow.user.query.mapper;

import com.ideality.coreflow.user.query.dto.UserOfRoleDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    List<UserOfRoleDTO> selectUserOfGeneralRole(Long userId);

    Long countByHireMonthAndDeptName(String deptName, String year);

    List<Long> selectAllUserByDeptName(String deptName);

<<<<<<< HEAD
    Long selectLeaderByDeptName(String deptName);

    long countByJobRoleName(String roleName);
=======
    List<Long> selectLeadersByDeptName(String deptName);
>>>>>>> develop
}
