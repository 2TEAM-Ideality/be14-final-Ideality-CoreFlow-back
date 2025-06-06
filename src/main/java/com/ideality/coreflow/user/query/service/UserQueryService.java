package com.ideality.coreflow.user.query.service;

import com.ideality.coreflow.user.query.dto.UserNameIdDto;

import java.util.List;

public interface UserQueryService {
    List<Long> selectMentionUserByDeptName(String deptName);

    List<UserNameIdDto> searchUsersByName(String name);

    List<Long> selectLeadersByDeptName(String deptName);

    String getDeptNameByUserId(Long userId);

    Boolean selectUserById(Long userId);

    List<String> selectMentionUserByProjectId(Long projectId);

    List<String> selectMentionUserByDeptName(List<String> mentionParse, Long projectId);

    List<String> selectMentionUserByDeptAndJob(List<String> mentionParse, Long projectId);

    List<String> selectMentionUserByMentionInfo(List<String> mentionParse, Long projectId);
}
