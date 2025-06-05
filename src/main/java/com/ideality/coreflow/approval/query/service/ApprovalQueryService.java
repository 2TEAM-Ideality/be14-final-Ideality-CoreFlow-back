package com.ideality.coreflow.approval.query.service;

import com.ideality.coreflow.approval.query.dto.ApprovalDetailedDTO;
import com.ideality.coreflow.approval.query.dto.ResponsePreviewApproval;
import com.ideality.coreflow.approval.query.dto.ResponseApproval;

import java.util.List;

public interface ApprovalQueryService {
    List<ResponsePreviewApproval> searchMyApprovalReceive(long id);

    List<ResponsePreviewApproval> searchApprovalByTaskId(long taskId);

    ApprovalDetailedDTO searchApprovalById(long id);

    long searchApproverIdByApprovalId(long approvalId);

    List<ResponsePreviewApproval> searchMyApprovalSent(long id);
}
