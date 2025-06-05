package com.ideality.coreflow.approval.query.dto;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApprovalDetailsDTO {
    long id;
    String requesterName;
    String title;
    ApprovalType type;
    ApprovalStatus status;
    String content;
    LocalDateTime createdAt;
    LocalDateTime approvedAt;

    long taskId;
    String taskName;
    String projectName;

    Integer delayDays;
    String actionDetail;
    String delayReason;
}
