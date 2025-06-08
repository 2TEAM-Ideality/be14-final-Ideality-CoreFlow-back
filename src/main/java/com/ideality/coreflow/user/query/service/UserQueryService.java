package com.ideality.coreflow.user.query.service;

<<<<<<< HEAD
import com.ideality.coreflow.user.query.dto.DeptNameAndYearDTO;
=======
import com.ideality.coreflow.user.query.dto.UserNameIdDto;
>>>>>>> develop

import java.util.List;

public interface UserQueryService {

    List<String> findGeneralRolesByUserId(Long userId);

    Long countByHireYearAndDeptName(DeptNameAndYearDTO countByDeptNameAndYearDTO);

    List<Long> selectMentionUserByDeptName(String deptName);

    List<UserNameIdDto> searchUsersByName(String name);

    long countByJobRoleName(String roleName);

    List<Long> selectLeadersByDeptName(String deptName);

    String getDeptNameByUserId(Long userId);

    Boolean selectUserById(Long userId);

    List<String> selectMentionUserByMentionInfo(List<String> mentionParse, Long projectId);
}
