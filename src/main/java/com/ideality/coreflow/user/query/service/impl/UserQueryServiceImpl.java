package com.ideality.coreflow.user.query.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.user.query.dto.*;
import com.ideality.coreflow.user.query.mapper.UserMapper;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public long countByJobRoleName(String roleName) {
        return userMapper.countByJobRoleName(roleName);
    }

    @Override
    public List<String> findGeneralRolesByUserId(Long userId) {
        List<UserOfRoleDTO> find = userMapper.selectUserOfGeneralRole(userId);
        return find.stream().map(UserOfRoleDTO::getRoleName).collect(Collectors.toList());
    }

    @Override
    public Long countByHireYearAndDeptName(DeptNameAndYearDTO countByDeptNameAndYearDTO) {

        return userMapper.countByHireMonthAndDeptName(countByDeptNameAndYearDTO.getDeptName(),
                countByDeptNameAndYearDTO.getHireDate().format(DateTimeFormatter.ofPattern("yy")));
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
    public List<String> selectMentionUserByMentionInfo(List<String> mentionParse, Long projectId) {
        List<UserMentionDTO> resultSet;

        if (mentionParse == null || mentionParse.isEmpty()) {
            // @만 입력된 경우, 프로젝트 참여자 전체 조회
            resultSet = userMapper.selectMentionUserByProjectId(projectId);
        } else if (mentionParse.size() == 1) {
            // 여기가 keyword 검색?
            String keyword = mentionParse.get(0);
            resultSet = userMapper.selectMentionUserByKeyword(keyword, projectId);
        } else {
            // 정확하게 파싱 조회
            String deptName = null;
            String jobRank = null;
            String name = null;

            if (mentionParse.size() >= 1) deptName = mentionParse.get(0);
            if (mentionParse.size() >= 2) jobRank = mentionParse.get(1);
            if (mentionParse.size() >= 3) name = mentionParse.get(2);

            resultSet = userMapper.selectMentionUserByMentionInfo(deptName, jobRank, name, projectId);
        }

        List<String> result = new ArrayList<>();
        for (UserMentionDTO user : resultSet) {
            result.add(user.getDeptName() + "_" + user.getJobRank() + "_" + user.getName());
        }

        return result;
    }

    @Override
    public List<Long> selectIdByMentionList(List<String> mentions) {

        List<MentionConditionDTO> mentionConditionDTOS = new ArrayList<>();
        for (String mention : mentions) {
            String[] parse = mention.split("_");
            String dept = parse[0];
            String rank = parse.length > 1 ? parse[1] : "";
            String user = parse.length > 2 ? parse[2] : "";

            mentionConditionDTOS.add(new MentionConditionDTO(dept, rank, user));
        }
        return userMapper.selectUserListByMention(mentionConditionDTOS);
    }

    @Override
    public String getUserId(Long userId) {
        return userMapper.selectUserNameById(userId);
    }

    @Override
    public List<ResponseUser> findAllUsers() {
        return userMapper.selectAllUser();
    }
}
