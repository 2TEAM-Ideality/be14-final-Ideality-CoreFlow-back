package com.ideality.coreflow.user.command.domain.repository;

import com.ideality.coreflow.user.command.domain.aggregate.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String roleName);
}
