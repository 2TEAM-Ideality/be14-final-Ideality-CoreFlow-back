package com.ideality.coreflow.approval.query.dto;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApprovalDetailsDTO {
    long id;
    long requesterId;
    String requesterName;
    String title;
    ApprovalType type;
    ApprovalStatus status;
    String content;
    LocalDateTime createdAt;
    LocalDateTime approvedAt;
    String rejectReason;

    long taskId;
    String taskName;
    String projectName;

    Integer delayDays;
    String actionDetail;
    String delayReason;
}
