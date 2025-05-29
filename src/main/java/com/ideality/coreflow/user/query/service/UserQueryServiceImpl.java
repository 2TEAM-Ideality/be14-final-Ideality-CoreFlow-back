package com.ideality.coreflow.user.query.service;

import com.ideality.coreflow.user.query.dto.UserOfRoleDTO;
import com.ideality.coreflow.user.query.mapper.UserMapper;
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
    public List<String> findGeneralRolesByUserId(Long userId) {
        List<UserOfRoleDTO> find = userMapper.selectUserOfGeneralRole(userId);
        return find.stream().map(UserOfRoleDTO::getRoleName).collect(Collectors.toList());
    }
}
