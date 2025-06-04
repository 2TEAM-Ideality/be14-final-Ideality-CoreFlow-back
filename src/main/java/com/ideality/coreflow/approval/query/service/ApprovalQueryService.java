package com.ideality.coreflow.approval.query.service;

import com.ideality.coreflow.approval.query.dto.ResponseApprovalByTaskId;
import com.ideality.coreflow.approval.query.dto.ResponseQueryApproval;

import java.util.List;

public interface ApprovalQueryService {
    List<ResponseQueryApproval> searchMyApproval(long id);

    List<ResponseApprovalByTaskId> searchApprovalByTaskId(long taskId);

    ResponseQueryApproval getApprovalById(long id);
}
