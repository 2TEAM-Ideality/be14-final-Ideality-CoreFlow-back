package com.ideality.coreflow.approval.command.application.service;

import com.ideality.coreflow.approval.command.application.dto.*;
import com.ideality.coreflow.approval.command.domain.aggregate.Approval;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import com.ideality.coreflow.approval.query.service.ApprovalQueryService;
import com.ideality.coreflow.attachment.command.application.dto.RegistAttachmentDTO;
import com.ideality.coreflow.attachment.command.application.service.AttachmentCommandService;
import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalFacadeService {

    private final ApprovalService approvalService;
    private final ApprovalQueryService approvalQueryService;
    private final ApprovalParticipantService approvalParticipantService;
    private final DelayApprovalService delayApprovalService;
    private final AttachmentCommandService attachmentCommandService;
    private final S3Service s3Service;

    @Transactional
    public void approve(RequestApprove request, long userId) {

        // 결재 id
        long approvalId = request.getApprovalId();
        Approval approval = approvalService.findApprovalById(approvalId);
        log.info("결재 id: {}", approvalId);

        long approverId = approvalQueryService.searchApproverIdByApprovalId(approvalId);
        log.info("결재자 id 조회: {}", approverId);

        // 해당 결재의 승인자가 현재 유저와 동일한지 검증
        if (approverId == userId && approval.getStatus() == ApprovalStatus.PENDING) {
            // 결재 승인
            approvalService.updateStatus(approval, ApprovalStatus.APPROVED);
            log.info("결재 승인");

            // 추가적인 열람자 설정
            for (long viewerId : request.getViewerIds()) {
                approvalParticipantService.registViewerById(viewerId, approvalId);
                log.info("열람자 등록");
            }
        } else {
            throw new BaseException(ErrorCode.ACCESS_DENIED_APPROVAL);
        }

    }

    @Transactional
    public void reject(RequestReject request, long userId) {

        long approvalId = request.getApprovalId();
        Approval approval = approvalService.findApprovalById(approvalId);

        long approverId = approvalQueryService.searchApproverIdByApprovalId(approvalId);

        if (approverId == userId && approval.getStatus() == ApprovalStatus.PENDING) {
            // 결재 반려 처리
            approvalService.updateStatus(approval, ApprovalStatus.REJECTED);

            // 반려 사유 업데이트
            approvalService.updateRejectReson(approval, request.getReason());
        } else {
            throw new BaseException(ErrorCode.ACCESS_DENIED_APPROVAL);
        }
    }

    @Transactional
    public long requestApproval(RequestApproval request, long requesterId) {

        // 결재 정보 등록
        CreateApprovalDTO approval = CreateApprovalDTO.builder()
                .requesterId(requesterId)
                .title(request.getTitle())
                .workId(request.getTaskId())
                .type(request.getType())
                .content(request.getContent())
                .build();

        long approvalId = approvalService.registApproval(approval);
        log.info("결재 정보 등록: {}", approvalId);

        // 참여자 등록
        CreateApprovalParticipantDTO approvalParticipant = CreateApprovalParticipantDTO.builder()
                .approvalId(approvalId)
                .approverId(request.getApproverId())
                .viewerIds(request.getViewerIds())
                .build();

        approvalParticipantService.registApprovalParticipant(approvalParticipant);
        log.info("참여자 등록 완료");

        // 지연 결재면 지연 정보 등록
        if (request.getType() == ApprovalType.DELAY) {
            CreateDelayApprovalDTO createDelayApproval = CreateDelayApprovalDTO.builder()
                    .delayDays(request.getDelayDays())
                    .actionDetail(request.getActionDetail())
                    .delayReasonId(request.getDelayReasonId())
                    .approvalId(approvalId)
                    .build();

            delayApprovalService.registDelayApproval(createDelayApproval);
            log.info("지연 정보 등록 완료");
        }

        return approvalId;
    }

    @Transactional
    public void uploadApprovalAttachment(long approvalId, MultipartFile file, long uploaderId) {
        String fileName = s3Service.generateFileName(file);
        log.info("파일 저장 명 {}", fileName);
        String folder = "approval-docs";

        String url = s3Service.uploadFileWithFileName(file, fileName, folder);
        log.info("파일 업로드 완료 {}", url);

        String size = file.getSize() + " bytes";

        RegistAttachmentDTO approvalAttachment = RegistAttachmentDTO.builder()
                .originName(file.getOriginalFilename())
                .storedName(fileName)
                .url(url)
                .fileType(file.getContentType())
                .size(size)
                .targetId(approvalId)
                .uploaderId(uploaderId)
                .targetType(FileTargetType.APPROVAL)
                .build();

        attachmentCommandService.registAttachment(approvalAttachment);
    }
}
