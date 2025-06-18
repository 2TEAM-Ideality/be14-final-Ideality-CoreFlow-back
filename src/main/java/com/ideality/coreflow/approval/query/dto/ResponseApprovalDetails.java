package com.ideality.coreflow.approval.query.dto;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import com.ideality.coreflow.attachment.command.application.dto.AttachmentPreviewDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class ResponseApprovalDetails {
    long id;
    long requesterId;
    String requesterName;
    String title;
    ApprovalType type;
    ApprovalStatus status;
    String content;
    LocalDateTime createdAt;
    LocalDateTime approvedAt;
    String rejectReason;

    long taskId;
    String taskName;
    String projectName;

    Integer delayDays;
    String actionDetail;
    String delayReason;

    List<AttachmentPreviewDTO> attachmentPreviewInfo;

    List<ApprovalParticipantDTO> approvalParticipants;

    // 지연 예상 정보
    LocalDate originProjectEndExpect;
    LocalDate newProjectEndExpect;
    int delayDaysByTask;
    int taskCountByDelay;
    Map<Long, DelayTaskNameDTO> delayDaysByTaskName;

    public void delayExpect(
            LocalDate originProjectEndExpect,
            LocalDate newProjectEndExpect,
            int delayDaysByTask,
            int taskCountByDelay,
            Map<Long, DelayTaskNameDTO> delayDaysByTaskName
            ) {
        this.originProjectEndExpect = originProjectEndExpect;
        this.newProjectEndExpect = newProjectEndExpect;
        this.delayDaysByTask = delayDaysByTask;
        this.taskCountByDelay = taskCountByDelay;
        this.delayDaysByTaskName = delayDaysByTaskName;
    }
}
