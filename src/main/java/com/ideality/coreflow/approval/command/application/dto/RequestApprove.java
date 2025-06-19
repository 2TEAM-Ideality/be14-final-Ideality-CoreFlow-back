package com.ideality.coreflow.approval.command.application.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RequestApprove {
    long approvalId;
    List<Long> viewerIds;
    int delayDays;
}
