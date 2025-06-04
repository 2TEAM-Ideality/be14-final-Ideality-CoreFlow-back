package com.ideality.coreflow.approval.query.service;

import com.ideality.coreflow.approval.query.dto.ResponseQueryApproval;

import java.util.List;

public interface ApprovalQueryService {
    List<ResponseQueryApproval> searchMayApproval(long id);
}
