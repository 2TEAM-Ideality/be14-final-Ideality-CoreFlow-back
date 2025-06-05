package com.ideality.coreflow.approval.query.service.impl;

import com.ideality.coreflow.approval.query.dto.ApprovalDetailedDTO;
import com.ideality.coreflow.approval.query.dto.ResponseApprovalByTaskId;
import com.ideality.coreflow.approval.query.dto.ResponseApproval;
import com.ideality.coreflow.approval.query.mapper.ApprovalMapper;
import com.ideality.coreflow.approval.query.service.ApprovalQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalQueryServiceImpl implements ApprovalQueryService {

    private final ApprovalMapper approvalMapper;

    @Override
    @Transactional
    public List<ResponseApproval> searchMyApproval(long id) {
        return approvalMapper.selectMyApproval(id);
    }

    @Override
    @Transactional
    public List<ResponseApprovalByTaskId> searchApprovalByTaskId(long taskId) {
        return approvalMapper.selectApprovalByTaskId(taskId);
    }

    @Override
    public ApprovalDetailedDTO searchApprovalById(long id) {
        return approvalMapper.selectApprovalById(id);
    }

    @Override
    public long searchApproverIdByApprovalId(long approvalId) {
        return approvalMapper.selectApprovalById(approvalId).getApproverId();
    }
}
