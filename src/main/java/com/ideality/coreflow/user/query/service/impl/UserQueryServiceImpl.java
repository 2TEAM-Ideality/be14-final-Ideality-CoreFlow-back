package com.ideality.coreflow.user.query.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.user.query.dto.DeptNameAndMonthDTO;
import com.ideality.coreflow.user.query.dto.UserOfRoleDTO;
import com.ideality.coreflow.user.query.mapper.UserMapper;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public Long selectLeaderByDeptName(String deptName) {
        return userMapper.selectLeaderByDeptName(deptName);
    }

    @Override
    public List<String> findGeneralRolesByUserId(Long userId) {
        List<UserOfRoleDTO> find = userMapper.selectUserOfGeneralRole(userId);
        return find.stream().map(UserOfRoleDTO::getRoleName).collect(Collectors.toList());
    }

    @Override
    public Long countByHireMonthAndDeptName(DeptNameAndMonthDTO countByDeptNameAndMonthDTO) {
        return userMapper.countByHireMonthAndDeptName(countByDeptNameAndMonthDTO.getYearMonth(), countByDeptNameAndMonthDTO.getDeptName());
    }
}
