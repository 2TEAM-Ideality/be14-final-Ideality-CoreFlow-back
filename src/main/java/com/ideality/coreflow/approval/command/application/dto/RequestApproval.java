package com.ideality.coreflow.approval.command.application.dto;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    List<MultipartFile> attachmentFile;
}
