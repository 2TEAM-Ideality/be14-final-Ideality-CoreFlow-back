package com.ideality.coreflow.user.query.mapper;

import com.ideality.coreflow.user.query.dto.UserMentionDTO;
import com.ideality.coreflow.user.query.dto.UserNameIdDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<Long> selectAllUserByDeptName(String deptName);

    // 이름으로 회원 조회 (id와 name만 반환)
    List<UserNameIdDto> searchUsersByName(String name);

    List<Long> selectLeadersByDeptName(String deptName);

    String selectDeptNameByUserId(Long userId);

    Boolean selectUserById(Long userId);

    List<UserMentionDTO> selectMentionUserByProjectId(Long projectId);

    List<UserMentionDTO> selectMentionUserByDeptAndJob(String deptName, String jobRank, Long projectId);

    List<UserMentionDTO> selectMentionUserByMentionInfo(String deptName, String jobRank, String name, Long projectId);

    List<UserMentionDTO> selectMentionUserByDeptName(String deptName, Long projectId);

    List<UserMentionDTO> selectMentionUserByKeyword(String keyword, Long projectId);
}
