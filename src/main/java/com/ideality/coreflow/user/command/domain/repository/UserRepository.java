package com.ideality.coreflow.user.command.domain.repository;

import com.ideality.coreflow.user.command.domain.aggregate.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmployeeNum(String employeeNum);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByDeptNameAndIsResign(String prevName, boolean b);

    List<User> findByJobRankNameAndIsResign(String prevName, boolean b);

    List<User> findByJobRoleNameAndIsResign(String prevName, boolean b);

    boolean existsByEmployeeNum(String employeeNum);
}
