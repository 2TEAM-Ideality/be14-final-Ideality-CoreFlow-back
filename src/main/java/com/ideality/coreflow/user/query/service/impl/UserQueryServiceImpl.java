package com.ideality.coreflow.user.query.service.impl;

import com.ideality.coreflow.user.command.domain.aggregate.User;
import com.ideality.coreflow.user.query.dto.ParticipantUserDTO;
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
    public List<ParticipantUserDTO> selectByDeptName(String deptName) {
        return userMapper.selectAllUserByDept(deptName);
    }
}
