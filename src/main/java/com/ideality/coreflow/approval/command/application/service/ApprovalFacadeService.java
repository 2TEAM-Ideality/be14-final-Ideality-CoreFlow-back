package com.ideality.coreflow.approval.command.application.service;

import com.ideality.coreflow.approval.command.application.dto.RequestApproval;
import com.ideality.coreflow.approval.query.dto.ApprovalDetailedDTO;
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

    public void approve(RequestApproval request, long userId) {

        // 결재 id
        long approvalId = request.getApprovalId();
        log.info("결재 id: {}", approvalId);

        long approverId = approvalQueryService.searchApproverIdByApprovalId(approvalId);
        log.info("결재자 id 조회: {}", approverId);

        // 해당 결재의 승인자가 현재 유저와 동일한지 검증
        if (approverId == userId) {
            // 결재 승인
            approvalService.approve(approvalId);
            log.info("결재 승인");

            // 추가적인 열람자 설정
            for (long viewerId : request.getViewerIds()) {
                approvalParticipantService.registViewerById(viewerId, approvalId);
                log.info("열람자 등록");
            }
        } else {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

    }
}
