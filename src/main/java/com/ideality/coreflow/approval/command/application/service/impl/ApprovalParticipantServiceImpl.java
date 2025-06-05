package com.ideality.coreflow.approval.command.application.service.impl;

import com.ideality.coreflow.approval.command.application.service.ApprovalParticipantService;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalParticipant;
import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalRole;
import com.ideality.coreflow.approval.command.domain.repository.ApprovalParticipantRepository;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApprovalParticipantServiceImpl implements ApprovalParticipantService {

    private final ApprovalParticipantRepository approvalParticipantRepository;


    @Override
    public void registViewerById(long viewerId, long approvalId) {

        ApprovalParticipant approvalParticipant = ApprovalParticipant.builder()
                .approvalId(approvalId)
                .userId(viewerId)
                .role(ApprovalRole.VIEWER)
                .createdAt(LocalDateTime.now())
                .build();

        approvalParticipantRepository.save(approvalParticipant);
    }
}
