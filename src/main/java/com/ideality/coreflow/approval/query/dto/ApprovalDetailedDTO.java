package com.ideality.coreflow.approval.query.dto;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalRole;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApprovalDetailedDTO {
    long id;
    long requesterId;
    String title;
    ApprovalType type;
    ApprovalStatus status;
    String content;
    LocalDateTime createdAt;
    LocalDateTime approvedAt;
    long approverId;

    Integer delayDays;
    String actionDetail;
    String reason;

    public void updateStatus(ApprovalStatus status) {
        this.status = status;
    }
}
