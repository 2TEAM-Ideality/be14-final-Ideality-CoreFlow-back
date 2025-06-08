package com.ideality.coreflow.approval.query.service;

import com.ideality.coreflow.approval.query.dto.ApprovalDetailsDTO;
import com.ideality.coreflow.approval.query.dto.ApprovalParticipantDTO;
import com.ideality.coreflow.approval.query.dto.ResponseApprovalDetails;
import com.ideality.coreflow.approval.query.dto.ResponsePreviewApproval;
import com.ideality.coreflow.attachment.command.application.dto.AttachmentPreviewDTO;
import com.ideality.coreflow.attachment.command.application.service.AttachmentCommandService;
import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import com.ideality.coreflow.attachment.query.service.AttachmentQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalQueryFacadeService {
    private final ApprovalQueryService approvalQueryService;
    private final AttachmentQueryService attachmentQueryService;
    private final AttachmentCommandService attachmentCommandService;

    public List<ResponsePreviewApproval> searchMyApprovalReceive(long id) {
        return approvalQueryService.searchMyApprovalReceive(id);
    }

    public List<ResponsePreviewApproval> searchMyApprovalSent(long id) {
        return approvalQueryService.searchMyApprovalSent(id);
    }

    public List<ResponsePreviewApproval> searchApprovalByTaskId(long taskId) {
        return approvalQueryService.searchApprovalByTaskId(taskId);
    }

    public ResponseApprovalDetails searchApprovalDetailsById(long approvalId, long userId) {

        // 읽기 권한 설정
        // 참여자인지 확인 -> 상세 내역 조회 가능자인지 확인하는 로직

        // 결재 상세 내역 조회
        ApprovalDetailsDTO approvalDetails = approvalQueryService.searchApprovalDetailsById(approvalId);

        // 결재 참여자 조회
        List<ApprovalParticipantDTO> approvalParticipant = approvalQueryService.searchApprovalParticipantById(approvalId);

        List<String> hasRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        log.info("회원의 역할: {}", hasRole);


        // 첨부파일 조회
        AttachmentPreviewDTO attachmentPreviewInfo = attachmentCommandService.getOriginName(approvalId, FileTargetType.APPROVAL);

        return ResponseApprovalDetails.builder()
                .id(approvalDetails.getId())
                .requesterName(approvalDetails.getRequesterName())
                .title(approvalDetails.getTitle())
                .type(approvalDetails.getType())
                .status(approvalDetails.getStatus())
                .content(approvalDetails.getContent())
                .createdAt(approvalDetails.getCreatedAt())
                .approvedAt(approvalDetails.getApprovedAt())
                .taskId(approvalDetails.getTaskId())
                .taskName(approvalDetails.getTaskName())
                .projectName(approvalDetails.getProjectName())
                .delayDays(approvalDetails.getDelayDays())
                .actionDetail(approvalDetails.getActionDetail())
                .delayReason(approvalDetails.getDelayReason())
                .attachmentUrl(attachmentPreviewInfo.getUrl())
                .originName(attachmentPreviewInfo.getOriginName())
                .build();
    }
}
