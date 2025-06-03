package com.ideality.coreflow.user.command.application.service.impl;

import com.ideality.coreflow.user.command.application.service.UserOfRoleService;
import com.ideality.coreflow.user.command.domain.aggregate.UserOfRole;
import com.ideality.coreflow.user.command.domain.repository.UserOfRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserOfRoleServiceImpl implements UserOfRoleService {

    private final UserOfRoleRepository userOfRoleRepository;

    @Override
    public void updateCreation(boolean isCreation, long userId, long roleId) {

        if (isCreation) {
            // 권한 추가
            UserOfRole userOfRole = UserOfRole.builder()
                    .userId(userId)
                    .roleId(roleId)
                    .build();
            userOfRoleRepository.save(userOfRole);
        } else {
            // 이미 권한이 부여되어 있다면 삭제, 없으면 그대로
            userOfRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
        }
    }
}
