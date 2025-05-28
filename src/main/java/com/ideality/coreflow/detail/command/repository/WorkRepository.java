package com.ideality.coreflow.detail.command.repository;

import com.ideality.coreflow.detail.command.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRepository extends JpaRepository<Work, Long> {
}
