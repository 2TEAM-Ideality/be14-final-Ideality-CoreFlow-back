package com.ideality.coreflow.approval.command.application.service;

import com.ideality.coreflow.approval.command.application.dto.CreateDelayApprovalDTO;

public interface DelayApprovalService {
    void registDelayApproval(CreateDelayApprovalDTO createDelayApproval);
}
