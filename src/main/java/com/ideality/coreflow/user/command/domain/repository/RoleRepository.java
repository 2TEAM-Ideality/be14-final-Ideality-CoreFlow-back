package com.ideality.coreflow.user.command.domain.repository;

import com.ideality.coreflow.user.command.domain.aggregate.Role;
import com.ideality.coreflow.user.command.domain.aggregate.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
