package com.ideality.coreflow.approval.query.dto;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalRole;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ResponseApproval {
    long id;
    long requesterId;
    String title;
    ApprovalType type;
    ApprovalStatus status;
    String content;
    LocalDateTime createdAt;
    LocalDateTime approvedAt;
    ApprovalRole myRole;

    Integer delayDays;
    String actionDetail;
    String reason;

    List<ResponseParticipant> participantList;
}
