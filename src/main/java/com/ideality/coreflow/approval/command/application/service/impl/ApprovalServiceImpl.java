package com.ideality.coreflow.approval.command.application.service.impl;

import com.ideality.coreflow.approval.command.application.dto.CreateApprovalDTO;
import com.ideality.coreflow.approval.command.application.service.ApprovalService;
import com.ideality.coreflow.approval.command.domain.aggregate.Approval;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;
import com.ideality.coreflow.approval.command.domain.repository.ApprovalRepository;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;

    public void updateStatus(Approval approval, ApprovalStatus status) {
        // 결재 승인
        approval.updateStatus(status);
        log.info("status 업데이트");
        approvalRepository.save(approval);
    }

    @Override
    public void updateRejectReson(Approval approval, String reason) {
        approval.updateReason(reason);
        approvalRepository.save(approval);
    }

    @Override
    public Approval findApprovalById(long approvalId) {
        return approvalRepository.findById(approvalId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));
    }

    @Override
    public long registApproval(CreateApprovalDTO approval) {
        Approval approvalEntity = Approval.builder()
                .userId(approval.getRequesterId())
                .title(approval.getTitle())
                .status(ApprovalStatus.PENDING)
                .type(approval.getType())
                .content(approval.getContent())
                .createdAt(LocalDateTime.now())
                .workId(approval.getWorkId())
                .build();
        return approvalRepository.save(approvalEntity).getId();
    }
}
