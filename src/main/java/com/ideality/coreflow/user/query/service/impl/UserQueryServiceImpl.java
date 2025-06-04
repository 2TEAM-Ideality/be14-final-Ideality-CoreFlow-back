package com.ideality.coreflow.user.query.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.template.query.dto.DeptDTO;
import com.ideality.coreflow.user.query.dto.UserNameIdDto;
import com.ideality.coreflow.user.query.mapper.UserMapper;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserMapper userMapper;

    @Override
    public List<Long> selectAllUserByDeptName(String deptName) {
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

    @Override
    public String getDeptNameByUserId(Long userId) {
        return userMapper.selectDeptNameByUserId(userId);
    }

}
