package com.ideality.coreflow.user.command.domain.repository;

import com.ideality.coreflow.user.command.domain.aggregate.UserOfRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOfRoleRepository extends JpaRepository<UserOfRole, Long> {

    void deleteByUserIdAndRoleId(long userId, long roleId);
}
