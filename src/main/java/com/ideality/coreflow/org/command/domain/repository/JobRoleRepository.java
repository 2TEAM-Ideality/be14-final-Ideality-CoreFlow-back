package com.ideality.coreflow.org.command.domain.repository;

import com.ideality.coreflow.org.command.domain.aggregate.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRoleRepository extends JpaRepository<JobRole, Integer> {
}
