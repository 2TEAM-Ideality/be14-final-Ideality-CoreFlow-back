package com.ideality.coreflow.approval.command.application.service;

import com.ideality.coreflow.approval.command.application.dto.RequestApprove;
import com.ideality.coreflow.approval.command.application.dto.RequestReject;
import com.ideality.coreflow.approval.command.domain.aggregate.Approval;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;
import com.ideality.coreflow.approval.query.service.ApprovalQueryService;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalFacadeService {

    private final ApprovalService approvalService;
    private final ApprovalQueryService approvalQueryService;
    private final ApprovalParticipantService approvalParticipantService;

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
}
