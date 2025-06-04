package com.ideality.coreflow.org.command.domain.repository;

import com.ideality.coreflow.org.command.domain.aggregate.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRoleRepository extends JpaRepository<JobRole, Integer> {
    Optional<JobRole> findByName(String prevJobRoleName);

    void deleteById(long id);
}
