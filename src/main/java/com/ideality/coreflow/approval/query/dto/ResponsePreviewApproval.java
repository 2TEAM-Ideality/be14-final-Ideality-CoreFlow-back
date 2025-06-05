package com.ideality.coreflow.approval.query.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ResponsePreviewApproval {
    long id;
    String requesterName;
    String approverName;
    String title;
    LocalDateTime approvedAt;
}
