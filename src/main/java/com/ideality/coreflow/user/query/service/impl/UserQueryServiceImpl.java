package com.ideality.coreflow.user.query.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.user.query.dto.DeptNameAndYearDTO;
import com.ideality.coreflow.user.query.dto.UserOfRoleDTO;
import com.ideality.coreflow.user.query.mapper.UserMapper;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
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
}
