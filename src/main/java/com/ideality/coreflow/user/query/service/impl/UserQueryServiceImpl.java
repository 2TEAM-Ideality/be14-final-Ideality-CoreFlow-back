package com.ideality.coreflow.user.query.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.user.query.dto.UserMentionDTO;
import com.ideality.coreflow.user.query.dto.UserNameIdDto;
import com.ideality.coreflow.user.query.mapper.UserMapper;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserMapper userMapper;

    @Override
    public List<Long> selectMentionUserByDeptName(String deptName) {
        List<Long> findUser = userMapper.selectAllUserByDeptName(deptName);
        if (findUser.isEmpty()) {
            throw new BaseException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }
        return findUser;
    }

    @Override
    public List<Long> selectLeadersByDeptName(String deptName) {
        return userMapper.selectLeadersByDeptName(deptName);
    }

    // 이름으로 회원 조회 (id, name만 반환)
    @Override
    public List<UserNameIdDto> searchUsersByName(String name) {
        return userMapper.searchUsersByName(name);
    }

    // UserId로 부서 이름 조회
    @Override
    public String getDeptNameByUserId(Long userId) {
        return userMapper.selectDeptNameByUserId(userId);
    }

    // UserId로 회원 조회
    @Override
    public Boolean selectUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public List<String> selectMentionUserByProjectId(Long projectId) {
        List<UserMentionDTO> userMentionInfo = userMapper.selectMentionUserByProjectId(projectId);

        List<String> returnMentionList = new ArrayList<>();

        for (UserMentionDTO userMentionDTO : userMentionInfo) {
            String name = userMentionDTO.getName();
            String jobRank = userMentionDTO.getJobRank();
            String deptName = userMentionDTO.getDeptName();

            returnMentionList.add(deptName + "_" + jobRank + "_" + name);
        }
        return returnMentionList;
    }

    @Override
    public List<String> selectMentionUserByDeptName(List<String> mentionParse, Long projectId) {
        String deptName = mentionParse.get(0);
        List<UserMentionDTO> resultSet = userMapper.selectMentionUserByDeptName(deptName,
                projectId);

        List<String> returnMentionList = new ArrayList<>();

        for (UserMentionDTO userMentionDTO : resultSet) {
            String selectName = userMentionDTO.getName();
            String selectJobRank = userMentionDTO.getJobRank();
            String selectDeptName = userMentionDTO.getDeptName();

            returnMentionList.add(selectDeptName + "_" + selectJobRank + "_" + selectName);
        }
        return returnMentionList;
    }

    @Override
    public List<String> selectMentionUserByDeptAndJob(List<String> mentionParse, Long projectId) {
        String deptName = mentionParse.get(0);
        String jobRank = mentionParse.get(1);

        List<UserMentionDTO> resultSet = userMapper.selectMentionUserByDeptAndJob(deptName,
                jobRank,
                projectId);

        List<String> returnMentionList = new ArrayList<>();

        for (UserMentionDTO userMentionDTO : resultSet) {
            String selectName = userMentionDTO.getName();
            String selectJobRank = userMentionDTO.getJobRank();
            String selectDeptName = userMentionDTO.getDeptName();

            returnMentionList.add(selectDeptName + "_" + selectJobRank + "_" + selectName);
        }
        return returnMentionList;
    }

    @Override
    public List<String> selectMentionUserByMentionInfo(List<String> mentionParse, Long projectId) {

        String deptName = mentionParse.get(0);
        String jobRank = mentionParse.get(1);
        String name = mentionParse.get(2);
        List<UserMentionDTO> resultSet = userMapper.selectMentionUserByMentionInfo(deptName,
                jobRank,
                name,
                projectId);

        List<String> returnMentionList = new ArrayList<>();

        for (UserMentionDTO userMentionDTO : resultSet) {
            String selectName = userMentionDTO.getName();
            String selectJobRank = userMentionDTO.getJobRank();
            String selectDeptName = userMentionDTO.getDeptName();

            returnMentionList.add(selectDeptName + "_" + selectJobRank + "_" + selectName);
        }
        return returnMentionList;
    }

}
