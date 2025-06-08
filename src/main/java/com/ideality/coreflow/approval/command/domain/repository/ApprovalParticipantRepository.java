package com.ideality.coreflow.approval.command.domain.repository;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalParticipantRepository extends JpaRepository<ApprovalParticipant, Long> {
}
