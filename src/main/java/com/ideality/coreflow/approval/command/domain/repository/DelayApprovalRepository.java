package com.ideality.coreflow.approval.command.domain.repository;

import com.ideality.coreflow.approval.command.domain.aggregate.DelayApproval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DelayApprovalRepository extends JpaRepository<DelayApproval, Long> {
}
