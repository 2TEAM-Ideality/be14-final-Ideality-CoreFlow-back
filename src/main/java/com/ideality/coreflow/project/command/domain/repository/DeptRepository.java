package com.ideality.coreflow.project.command.domain.repository;

import com.ideality.coreflow.project.command.domain.aggregate.Dept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeptRepository extends JpaRepository<Dept, Long> {
}
