package com.ideality.coreflow.approval.command.domain.repository;

import com.ideality.coreflow.approval.command.domain.aggregate.DelayReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DelayReasonRepository extends JpaRepository<DelayReason, Long> {
}
