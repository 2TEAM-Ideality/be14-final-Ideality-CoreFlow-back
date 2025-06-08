package com.ideality.coreflow.approval.command.application.service;

import com.ideality.coreflow.approval.command.application.dto.CreateApprovalParticipantDTO;

public interface ApprovalParticipantService {
    void registViewerById(long viewerId, long approvalId);

    void registApprovalParticipant(CreateApprovalParticipantDTO approvalParticipant);
}
