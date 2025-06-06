package com.ideality.coreflow.approval.command.application.dto;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import lombok.Getter;

import java.util.List;

@Getter
public class RequestApproval {

    String title;
    long projectId;
    long taskId;
    ApprovalType type;
    String content;

    long approverId;
    List<Long> viewerIds;

    Integer delayDays;
    Long delayReasonId;
    String actionDetail;

    // 첨부파일 정보
}
