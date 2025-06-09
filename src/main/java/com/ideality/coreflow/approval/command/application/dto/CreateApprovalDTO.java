package com.ideality.coreflow.approval.command.application.dto;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateApprovalDTO {
    long requesterId;
    String title;
    long workId;
    ApprovalType type;
    String content;
}
