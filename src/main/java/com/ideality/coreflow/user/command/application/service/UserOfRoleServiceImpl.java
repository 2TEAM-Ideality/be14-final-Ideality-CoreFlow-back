package com.ideality.coreflow.user.command.application.service;

import com.ideality.coreflow.user.command.domain.aggregate.UserOfRole;
import com.ideality.coreflow.user.command.domain.repository.UserOfRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserOfRoleServiceImpl implements UserOfRoleService{

    private final UserOfRoleRepository userOfRoleRepository;

    @Override
    public void registAuthorities(long userId, long roleId) {
        UserOfRole userOfRole = UserOfRole.builder()
                                    .userId(userId)
                                    .roleId(roleId)
                                    .build();
        userOfRoleRepository.save(userOfRole);
    }
}
