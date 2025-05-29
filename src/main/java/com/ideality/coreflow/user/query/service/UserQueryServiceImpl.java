package com.ideality.coreflow.user.query.service;

import com.ideality.coreflow.user.query.dto.UserOfRoleDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserQueryServiceImpl implements UserQueryService {


    @Override
    public List<UserOfRoleDTO> findRolesByUserId(Long userId) {

        return List.of();
    }
}
