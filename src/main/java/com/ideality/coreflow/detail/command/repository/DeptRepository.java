package com.ideality.coreflow.detail.command.repository;

import com.ideality.coreflow.detail.command.entity.Dept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeptRepository extends JpaRepository<Dept, Long> {
}
