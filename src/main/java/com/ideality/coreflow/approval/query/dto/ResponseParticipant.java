package com.ideality.coreflow.approval.query.dto;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalRole;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ResponseParticipant {
    private long id;

    private long approvalId;

    private long userId;

    private ApprovalRole role;

    private LocalDateTime createdAt;
}
