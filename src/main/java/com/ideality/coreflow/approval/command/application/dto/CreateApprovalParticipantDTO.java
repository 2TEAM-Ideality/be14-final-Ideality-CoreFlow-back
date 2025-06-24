package com.ideality.coreflow.approval.command.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
public class CreateApprovalParticipantDTO {
    long approvalId;
    long approverId;
    List<Long> viewerIds;

}
