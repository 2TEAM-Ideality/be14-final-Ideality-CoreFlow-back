package com.ideality.coreflow.approval.command.application.dto;

import lombok.Getter;

@Getter
public class RequestReject {
    long approvalId;
    String reason;
}
