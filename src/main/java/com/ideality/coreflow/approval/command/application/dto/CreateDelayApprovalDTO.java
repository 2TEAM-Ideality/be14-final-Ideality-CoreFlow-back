package com.ideality.coreflow.approval.command.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateDelayApprovalDTO {

    long approvalId;
    Integer delayDays;
    long delayReasonId;
    String actionDetail;

    public void updateApprovalId(long approvalId) {
        this.approvalId = approvalId;
    }
}
