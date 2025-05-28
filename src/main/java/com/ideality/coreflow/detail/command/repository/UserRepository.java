package com.ideality.coreflow.detail.command.repository;

import com.ideality.coreflow.detail.command.entity.Relation;
import com.ideality.coreflow.detail.command.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
