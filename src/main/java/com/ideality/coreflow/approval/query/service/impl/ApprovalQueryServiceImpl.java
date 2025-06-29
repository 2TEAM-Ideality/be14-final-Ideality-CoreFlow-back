package com.ideality.coreflow.approval.query.service.impl;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import com.ideality.coreflow.approval.query.dto.ApprovalDetailsDTO;
import com.ideality.coreflow.approval.query.dto.ApprovalParticipantDTO;
import com.ideality.coreflow.approval.query.dto.ProjectApprovalDTO;
import com.ideality.coreflow.approval.query.dto.ResponsePreviewApproval;
import com.ideality.coreflow.approval.query.mapper.ApprovalMapper;
import com.ideality.coreflow.approval.query.service.ApprovalQueryService;
import com.ideality.coreflow.attachment.query.dto.GetDeptInfoDTO;
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
    public List<ResponsePreviewApproval> searchMyApprovalReceive(long userId) {
        return approvalMapper.selectMyApprovalReceive(userId);
    }

    @Override
    @Transactional
    public List<ResponsePreviewApproval> searchApprovalByTaskId(long taskId) {
        return approvalMapper.selectApprovalByTaskId(taskId);
    }

    @Override
    public long searchApproverIdByApprovalId(long approvalId) {
        return approvalMapper.selectApprovedApprovalById(approvalId);
    }

    @Override
    public List<ResponsePreviewApproval> searchMyApprovalSent(long userId) {
        return approvalMapper.selectMyApprovalSent(userId);
    }

    @Override
    public List<ApprovalParticipantDTO> searchApprovalParticipantById(long approvalId) {
        return approvalMapper.selectApprovalParticipantById(approvalId);
    }

    @Override
    public ApprovalDetailsDTO searchApprovalDetailsById(long approvalId) {
        return approvalMapper.selectApprovalDetailsById(approvalId);
    }

    @Override
    public List<ProjectApprovalDTO> selectProjectApprovalByProjectId(long projectId, ApprovalType approvalType) {
        return approvalMapper.selectProjectApprovalByProjectId(projectId, approvalType);
    }

    @Override
    @Transactional
    public List<GetDeptInfoDTO> selectProjectDeliverable(List<Long> uploaderIdList) {
        return approvalMapper.selectProjectDeliverable(uploaderIdList);
    }
}
