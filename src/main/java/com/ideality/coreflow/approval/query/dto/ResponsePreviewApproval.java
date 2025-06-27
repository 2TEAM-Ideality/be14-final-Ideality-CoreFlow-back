package com.ideality.coreflow.approval.query.dto;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ResponsePreviewApproval {
    long id;
    String requesterName;
    String approverName;
    String title;
    LocalDateTime approvedAt;
    ApprovalType approvalType;
    ApprovalStatus approvalStatus;
    String projectName;
}
