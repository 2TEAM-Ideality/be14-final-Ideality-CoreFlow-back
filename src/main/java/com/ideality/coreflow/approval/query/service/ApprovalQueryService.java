package com.ideality.coreflow.approval.query.service;

import com.ideality.coreflow.approval.query.dto.ApprovalDetailsDTO;
import com.ideality.coreflow.approval.query.dto.ApprovalParticipantDTO;
import com.ideality.coreflow.approval.query.dto.ResponsePreviewApproval;

import java.util.List;

public interface ApprovalQueryService {
    List<ResponsePreviewApproval> searchMyApprovalReceive(long userId);

    List<ResponsePreviewApproval> searchApprovalByTaskId(long taskId);

    long searchApproverIdByApprovalId(long approvalId);

    List<ResponsePreviewApproval> searchMyApprovalSent(long userId);

    List<ApprovalParticipantDTO> searchApprovalParticipantById(long approvalId);

    ApprovalDetailsDTO searchApprovalDetailsById(long approvalId);
}
