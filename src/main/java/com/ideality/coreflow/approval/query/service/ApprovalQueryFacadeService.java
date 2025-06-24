package com.ideality.coreflow.approval.query.service;

import com.ideality.coreflow.approval.command.application.service.DelayReasonService;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import com.ideality.coreflow.approval.command.domain.aggregate.DelayReason;
import com.ideality.coreflow.approval.query.dto.*;
import com.ideality.coreflow.attachment.command.application.dto.AttachmentPreviewDTO;
import com.ideality.coreflow.attachment.command.application.service.AttachmentCommandService;
import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import com.ideality.coreflow.attachment.query.service.AttachmentQueryService;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.approval.command.application.dto.DelayInfoDTO;
import com.ideality.coreflow.project.command.application.service.ParticipantService;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.command.domain.service.DelayDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalQueryFacadeService {
    private final ApprovalQueryService approvalQueryService;
    private final AttachmentCommandService attachmentCommandService;
    private final DelayReasonService delayReasonService;
    private final ParticipantService participantService;
    private final TaskService taskService;
    private final DelayDomainService delayDomainService;

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

        // 결재 상세 내역 조회
        ApprovalDetailsDTO approvalDetails = approvalQueryService.searchApprovalDetailsById(approvalId);

        // 결재 참여자 조회
        List<ApprovalParticipantDTO> approvalParticipant = approvalQueryService.searchApprovalParticipantById(approvalId);

        // 결재 참여자인지 아닌지
        boolean isParticipant = approvalParticipant.stream()
                .anyMatch(participant -> Objects.equals(participant.getParticipantId(), userId));

        log.info("참여자인지 아닌지: {}", isParticipant);

        // 태스크 참여자인지 아닌지 확인 로직 추가?
        boolean isTaskParticipant = participantService.isParticipant(approvalDetails.getTaskId(), userId, TargetType.TASK);

        log.info("태스크 참여자인지 아닌지:{}", isTaskParticipant);

        if (!isParticipant && !isTaskParticipant && approvalDetails.getRequesterId() != userId) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }


        // 첨부파일 조회
        List<AttachmentPreviewDTO> attachmentPreviewInfo = attachmentCommandService.getOriginNameList(approvalId, FileTargetType.APPROVAL);

        ResponseApprovalDetails response = ResponseApprovalDetails.builder()
                                                .id(approvalDetails.getId())
                                                .requesterId(approvalDetails.getRequesterId())
                                                .requesterName(approvalDetails.getRequesterName())
                                                .requesterDeptName(approvalDetails.getRequesterDeptName())
                                                .title(approvalDetails.getTitle())
                                                .type(approvalDetails.getType())
                                                .status(approvalDetails.getStatus())
                                                .content(approvalDetails.getContent())
                                                .createdAt(approvalDetails.getCreatedAt())
                                                .approvedAt(approvalDetails.getApprovedAt())
                                                .rejectReason(approvalDetails.getRejectReason())
                                                .taskId(approvalDetails.getTaskId())
                                                .taskName(approvalDetails.getTaskName())
                                                .projectName(approvalDetails.getProjectName())
                                                .delayDays(approvalDetails.getDelayDays())
                                                .actionDetail(approvalDetails.getActionDetail())
                                                .delayReason(approvalDetails.getDelayReason())
                                                .attachmentPreviewInfo(attachmentPreviewInfo)
                                                .approvalParticipants(approvalParticipant)
                                                .build();

        // 지연일 경우 지연 예상 정보 조회
        if (approvalDetails.getType() == ApprovalType.DELAY && approvalDetails.getStatus() == ApprovalStatus.PENDING) {
            DelayInfoDTO delayInfoDTO =  delayDomainService.delayAndPropagateLogic(approvalDetails.getTaskId(), approvalDetails.getDelayDays(), true);

            Map<Long, DelayTaskNameDTO> result = new HashMap<>();

            for (Map.Entry<Long, Integer> entry : delayInfoDTO.getDelayDaysByTaskId().entrySet()) {
                long taskId = entry.getKey();
                Integer delayDays = entry.getValue();

                String taskName = taskService.findTaskNameById(taskId);

                result.put(taskId, new DelayTaskNameDTO(taskName, delayDays));
            }
            response.delayExpect(
                    delayInfoDTO.getOriginProjectEndExpect(),
                    delayInfoDTO.getNewProjectEndExpect(),
                    delayInfoDTO.getDelayDaysByTask(),
                    delayInfoDTO.getTaskCountByDelay(),
                    result
            );
        }
        return response;
    }

    public ResponseAllPreviewApproval searchMyApprovalAll(long id) {
        List<ResponsePreviewApproval> receivedApproval = approvalQueryService.searchMyApprovalReceive(id);
        List<ResponsePreviewApproval> sentApproval = approvalQueryService.searchMyApprovalSent(id);
        return ResponseAllPreviewApproval.builder()
                .receivedApproval(receivedApproval)
                .sentApproval(sentApproval)
                .build();
    }

    public List<DelayReason> searchDelayReason() {
        return delayReasonService.findAllDelayReason();
    }

    public List<ProjectApprovalDTO> searchDelayListByProjectId(long projectId) {
        return approvalQueryService.selectProjectApprovalByProjectId(projectId, ApprovalType.DELAY);
    }
}
