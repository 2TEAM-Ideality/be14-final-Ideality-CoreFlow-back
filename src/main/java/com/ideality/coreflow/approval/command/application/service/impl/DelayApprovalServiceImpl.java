package com.ideality.coreflow.approval.command.application.service.impl;

import com.ideality.coreflow.approval.command.application.dto.CreateDelayApprovalDTO;
import com.ideality.coreflow.approval.command.application.service.DelayApprovalService;
import com.ideality.coreflow.approval.command.domain.aggregate.DelayApproval;
import com.ideality.coreflow.approval.command.domain.repository.DelayApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DelayApprovalServiceImpl implements DelayApprovalService {

    private final DelayApprovalRepository delayApprovalRepository;

    @Override
    public void registDelayApproval(CreateDelayApprovalDTO createDelayApproval) {

        DelayApproval delayApproval = DelayApproval.builder()
                .delayDays(createDelayApproval.getDelayDays())
                .actionDetail(createDelayApproval.getActionDetail())
                .delayReasonId(createDelayApproval.getDelayReasonId())
                .approvalId(createDelayApproval.getApprovalId())
                .build();

        delayApprovalRepository.save(delayApproval);
    }
}
