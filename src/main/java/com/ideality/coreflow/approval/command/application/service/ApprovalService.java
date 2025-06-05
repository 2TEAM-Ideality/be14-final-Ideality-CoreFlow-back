package com.ideality.coreflow.approval.command.application.service;

import com.ideality.coreflow.approval.command.domain.aggregate.Approval;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;

public interface ApprovalService {

    void updateStatus(Approval approval, ApprovalStatus status);

    void updateRejectReson(Approval approval, String reason);

    Approval findApprovalById(long approvalId);
}
