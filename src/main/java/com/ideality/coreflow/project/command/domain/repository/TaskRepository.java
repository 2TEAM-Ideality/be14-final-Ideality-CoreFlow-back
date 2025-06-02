package com.ideality.coreflow.project.command.domain.repository;

import com.ideality.coreflow.project.command.domain.aggregate.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Work, Long> {
}
