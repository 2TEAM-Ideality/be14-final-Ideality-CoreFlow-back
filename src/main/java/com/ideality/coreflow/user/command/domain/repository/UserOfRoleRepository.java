package com.ideality.coreflow.user.command.domain.repository;

import com.ideality.coreflow.user.command.domain.aggregate.UserOfRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOfRoleRepository extends JpaRepository<UserOfRole, Long> {

    List<UserOfRole> findByUserId(Long userId);
}
