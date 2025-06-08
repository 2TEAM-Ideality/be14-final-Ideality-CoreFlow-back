package com.ideality.coreflow.approval.command.domain.repository;

import com.ideality.coreflow.approval.command.domain.aggregate.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
}
