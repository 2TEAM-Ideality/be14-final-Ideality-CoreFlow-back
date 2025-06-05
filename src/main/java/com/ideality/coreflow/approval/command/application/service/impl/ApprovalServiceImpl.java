package com.ideality.coreflow.approval.command.application.service.impl;

import com.ideality.coreflow.approval.command.application.service.ApprovalService;
import com.ideality.coreflow.approval.command.domain.aggregate.Approval;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;
import com.ideality.coreflow.approval.command.domain.repository.ApprovalRepository;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;

    @Override
    public void approve(long approvalId) {
        log.info("approve 시작");
        Approval approval = approvalRepository.findById(approvalId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));
        log.info("approve 조회해옴");
        // 결재 승인
        approval.approve(ApprovalStatus.APPROVED);
        log.info("status 업데이트");
        approvalRepository.save(approval);
    }
}
