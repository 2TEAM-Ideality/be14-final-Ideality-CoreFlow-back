package com.ideality.coreflow.project.command.domain.repository;

import com.ideality.coreflow.project.command.domain.aggregate.WorkDept;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkDeptRepository extends JpaRepository<WorkDept,Long> {
    Optional<WorkDept> findByWorkId(Long detailId);  // taskId로 부서 정보 조회
}
