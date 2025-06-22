package com.ideality.coreflow.user.query.mapper;

import com.ideality.coreflow.user.query.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    List<UserOfRoleDTO> selectUserOfGeneralRole(Long userId);

    Long countByHireMonthAndDeptName(String deptName, String year);

    List<Long> selectAllUserByDeptName(String deptName);

    long countByJobRoleName(String roleName);

    // 이름으로 회원 조회 (id와 name만 반환)
    List<UserNameIdDto> searchUsersByName(String name);

    List<Long> selectLeadersByDeptName(String deptName);

    String selectDeptNameByUserId(Long userId);

    Boolean selectUserById(Long userId);

    List<UserMentionDTO> selectMentionUserByProjectId(Long projectId);

    List<UserMentionDTO> selectMentionUserByMentionInfo(String deptName, String name, Long projectId);

    List<UserMentionDTO> selectMentionUserByKeyword(String keyword, Long projectId);

    List<Long> selectUserListByMention(List<MentionConditionDTO> mentionConditionDTOS);

    String selectUserNameById(Long userId);

    List<ResponseUser> selectAllUserPreView();

    List<AllUserDTO> selectAllUser();

    List<String> selectDeptNameByMentionInfo(String mentionTarget, Long projectId);

    List<String> selectDeptNamesByProjectId(Long projectId);

    List<UserNameIdDto> getUsersByDept(String deptName);
}
