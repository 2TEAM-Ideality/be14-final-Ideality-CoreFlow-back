package com.ideality.coreflow.approval.query.dto;

import java.time.LocalDateTime;

public class ResponseApprovalByTaskId {
    long id;
    String requesterName;
    String title;
    LocalDateTime approvedAt;
}
