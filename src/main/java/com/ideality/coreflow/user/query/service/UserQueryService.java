package com.ideality.coreflow.user.query.service;

import com.ideality.coreflow.user.query.dto.*;

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

    List<UserMentionDTO> selectMentionUserByMentionInfo(List<String> mentionParse, Long projectId);

    List<Long> selectIdByMentionList(List<String> mentions);

    String getUserId(Long userId);

    List<ResponseUser> findAllUsers();

    List<AllUserDTO> selectAllUser();

    List<String> selectTeamByMentionInfo(List<String> mentionParse, Long projectId);

    List<UserNameIdDto> getUsersByDept(String deptName);
}
