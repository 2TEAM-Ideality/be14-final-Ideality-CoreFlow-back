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
    public List<Long> selectLeadersByDeptName(Long projectId, String deptName) {
        return userMapper.selectLeadersByDeptName(projectId, deptName);
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


    /* 설명. 댓글에서 입력값에 따른 유저 리스트 반환하는 코드 */
    @Override
    public List<UserMentionDTO> selectMentionUserByMentionInfo(List<String> mentionParse, Long projectId) {
        MentionSearchParamDTO dto = new MentionSearchParamDTO();
        dto.setProjectId(projectId);

        if (mentionParse != null && mentionParse.size() == 2) {
            dto.setDeptName(mentionParse.get(0)); // ex: "생산팀"
            dto.setName(mentionParse.get(1));     // ex: "한"
        } else if (mentionParse != null && !mentionParse.isEmpty()) {
            dto.setKeyword(mentionParse.get(0));  // ex: "생산팀_한" or "한"
        }
        return userMapper.selectMentionUserByKeyword(dto);
    }

    /* 설명. 댓글 작성에서 멘션에서 들어온 댓글 내용이 존재하는가 */
    @Override
    public List<Long> selectIdByMentionList(List<String> mentions, Long projectId) {

        List<MentionConditionDTO> mentionConditionDTOS = new ArrayList<>();
        for (String mention : mentions) {
            String[] parse = mention.split("_");
            String dept = parse[0];
            String user = parse.length > 1 ? parse[1] : null; // null로 구분 -> 태그가 디자인 팀일 경우

            mentionConditionDTOS.add(new MentionConditionDTO(projectId, dept, user));
        }
        return userMapper.selectUserListByMention(mentionConditionDTOS);
    }

    @Override
    public String getUserName(Long userId) {
        return userMapper.selectUserNameById(userId);
    }

    @Override
    public List<ResponseUser> findAllUsers() {
        return userMapper.selectAllUserPreView();
    }

    @Override
    public List<AllUserDTO> selectAllUser() {
        return userMapper.selectAllUser();
    }

    @Override
    public List<String> selectTeamByMentionInfo(List<String> mentionParse, Long projectId) {
        String mentionTarget = (mentionParse == null || mentionParse.isEmpty()) ? null : mentionParse.get(0);
        return userMapper.selectDeptNameByMentionInfo(mentionTarget, projectId);
    }

@Override
public List<UserNameIdDto> getUsersByDept(String deptName){
        return userMapper.getUsersByDept(deptName);
}
}
