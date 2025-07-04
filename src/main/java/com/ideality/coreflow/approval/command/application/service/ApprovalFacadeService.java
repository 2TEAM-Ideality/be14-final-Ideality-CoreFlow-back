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
import com.ideality.coreflow.holiday.query.dto.HolidayQueryDto;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.infra.s3.S3Service;
import com.ideality.coreflow.infra.s3.UploadFileResult;
import com.ideality.coreflow.notification.command.application.service.NotificationService;
import com.ideality.coreflow.notification.command.domain.aggregate.NotificationTargetType;
import com.ideality.coreflow.approval.command.application.dto.DelayInfoDTO;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.service.DelayDomainService;
import com.ideality.coreflow.project.command.domain.service.WorkDomainService;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import com.ideality.coreflow.project.query.service.ProjectQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalFacadeService {

    private final ApprovalService approvalService;
    private final ApprovalQueryService approvalQueryService;
    private final ApprovalParticipantService approvalParticipantService;
    private final DelayApprovalService delayApprovalService;
    private final AttachmentCommandService attachmentCommandService;
    private final NotificationService notificationService;
    private final S3Service s3Service;
    private final DelayDomainService delayDomainService;
    private final ParticipantQueryService participantQueryService;
    private final TaskService taskService;
    private final TaskQueryService taskQueryService;
    private final ProjectQueryService projectQueryService;
    private final WorkDomainService workDomainService;

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

            // 결재 승인 후 알림 전송
            String notificationContent = "결재가 승인되었습니다. [" + approval.getTitle() + "]";
            notificationService.sendNotification(
                    approval.getUserId(),    // 결재 요청자 ID
                    notificationContent,     // 알림 내용
                    approval.getId(),        // 대상 ID (결재 ID)
                    NotificationTargetType.APPROVAL      // 대상 타입 (결재)
            );

            // 추가적인 열람자 설정
            if (request.getViewerIds() != null) {
                for (long viewerId : request.getViewerIds()) {
                    approvalParticipantService.registViewerById(viewerId, approvalId);
                    log.info("열람자 등록");
                }
            }
        } else {
            throw new BaseException(ErrorCode.ACCESS_DENIED_APPROVAL);
        }
        if (approval.getType() == ApprovalType.DELAY) {
            // 지연 전파
            delayAndPropagate(approval.getWorkId(), request.getDelayDays(), false);
            workDomainService.updateTaskWarning(approval.getWorkId());
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

            // 결재 승인 후 알림 전송
            String notificationContent = "결재가 반려되었습니다. [" + approval.getTitle() + "]";
            notificationService.sendNotification(
                    approval.getUserId(),    // 결재 요청자 ID
                    notificationContent,     // 알림 내용
                    approval.getId(),        // 대상 ID (결재 ID)
                    NotificationTargetType.APPROVAL      // 대상 타입 (결재)
            );

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

        // 결재 요청을 받은 사람(결재자)에게 알림 전송
        String notificationContent = "새로운 결재 요청이 도착했습니다. [" + request.getTitle() + "]";
        notificationService.sendNotification(
                request.getApproverId(),    // 결재자 ID
                notificationContent,        // 알림 내용
                approvalId,                 // 대상 ID (결재 ID)
                NotificationTargetType.APPROVAL         // 대상 타입 (결재)
        );
        log.info("결재 요청자에게 알림 전송");

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

        // 첨부파일 업로드
        String folder = "approval-docs";

        if (request.getAttachmentFile() != null) {
            for (MultipartFile file : request.getAttachmentFile()) {
                UploadFileResult fileResult = s3Service.uploadFile(file, folder);

                RegistAttachmentDTO approvalAttachment = RegistAttachmentDTO.builder()
                        .originName(fileResult.getOriginalName())
                        .storedName(fileResult.getStoredName())
                        .url(fileResult.getUrl())
                        .fileType(fileResult.getFileType())
                        .size(fileResult.getSize())
                        .targetId(approvalId)
                        .uploaderId(requesterId)
                        .targetType(FileTargetType.APPROVAL)
                        .build();

                attachmentCommandService.registAttachment(approvalAttachment);
            }
        }

        return approvalId;
    }

    @Transactional
    public void cancelled(Long approvalId, long userId) {
        Approval approval = approvalService.findApprovalById(approvalId);
        log.info("approval Status: {}", approval.getStatus());

        if (approval.getUserId() != userId || approval.getStatus() != ApprovalStatus.PENDING) {
            throw new BaseException(ErrorCode.ACCESS_DENIED_APPROVAL);
        }
        approvalService.updateStatus(approval, ApprovalStatus.CANCELLED);
    }

    @Transactional
    DelayInfoDTO delayAndPropagate(Long taskId, Integer delayDays, boolean isSimulate) {

        String startTaskName = taskService.findTaskNameById(taskId);
        DelayInfoDTO delayInfo = delayDomainService.delayAndPropagateLogic(taskId, delayDays, isSimulate);

        // simulate가 아니고 실제로 지연된 태스크가 있다면 알림 처리
        if (!isSimulate) {
            Map<Long, Integer> delayedTaskMap = delayInfo.getDelayDaysByTaskId();

            for (Long delayedTaskId : delayedTaskMap.keySet()) {

                String delayedTaskName = taskService.findTaskNameById(delayedTaskId);

                List<Long> participantIds = participantQueryService.findParticipantsByTaskId(delayedTaskId);
                String content = "태스크 [" + startTaskName + "]가 지연되어 ["+ delayedTaskName +"]의 예상마감일이 변경되었습니다!";

                for (Long userId : participantIds) {
                    notificationService.sendNotification(userId, content, delayedTaskId, NotificationTargetType.WORK);
                }
            }
        }

        // 프로젝트 마감일이 바뀐 경우에만 디렉터에게 알림
        if (!delayInfo.getOriginProjectEndExpect().isEqual(delayInfo.getNewProjectEndExpect())) {
            long projectId = taskQueryService.getProjectId(taskId);
            String projectName = projectQueryService.getProjectName(projectId);

            Long directorId = participantQueryService.findDirectorByProjectId(projectId);
            String directorContent = "프로젝트 [" + projectName + "]의 예상 마감일이" + delayInfo.getDelayDaysByTask() + "일 지연되었습니다!";
            notificationService.sendNotification(directorId, directorContent, projectId, NotificationTargetType.PROJECT);
        }

        return delayInfo;
    }
}
