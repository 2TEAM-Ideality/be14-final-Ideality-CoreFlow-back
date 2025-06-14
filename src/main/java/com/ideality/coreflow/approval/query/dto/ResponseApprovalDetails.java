package com.ideality.coreflow.approval.query.dto;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import com.ideality.coreflow.attachment.command.application.dto.AttachmentPreviewDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ResponseApprovalDetails {
    long id;
    String requesterName;
    String title;
    ApprovalType type;
    ApprovalStatus status;
    String content;
    LocalDateTime createdAt;
    LocalDateTime approvedAt;

    long taskId;
    String taskName;
    String projectName;

    Integer delayDays;
    String actionDetail;
    String delayReason;

    List<AttachmentPreviewDTO> attachmentPreviewInfo;

    List<ApprovalParticipantDTO> approvalParticipants;
}
