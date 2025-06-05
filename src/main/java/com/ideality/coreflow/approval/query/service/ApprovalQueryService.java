package com.ideality.coreflow.approval.query.service;

import com.ideality.coreflow.approval.query.dto.ApprovalDetailedDTO;
import com.ideality.coreflow.approval.query.dto.ResponseApprovalByTaskId;
import com.ideality.coreflow.approval.query.dto.ResponseApproval;

import java.util.List;

public interface ApprovalQueryService {
    List<ResponseApproval> searchMyApproval(long id);

    List<ResponseApprovalByTaskId> searchApprovalByTaskId(long taskId);

    ApprovalDetailedDTO searchApprovalById(long id);

    long searchApproverIdByApprovalId(long approvalId);
}
