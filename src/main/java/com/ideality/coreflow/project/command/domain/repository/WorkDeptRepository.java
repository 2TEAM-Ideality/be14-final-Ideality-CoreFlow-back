package com.ideality.coreflow.project.command.domain.repository;

import com.ideality.coreflow.project.command.domain.aggregate.WorkDept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkDeptRepository extends JpaRepository<WorkDept, Long> {
}