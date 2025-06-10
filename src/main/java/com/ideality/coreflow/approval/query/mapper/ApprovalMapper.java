package com.ideality.coreflow.approval.query.mapper;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import com.ideality.coreflow.approval.query.dto.ApprovalDetailsDTO;
import com.ideality.coreflow.approval.query.dto.ApprovalParticipantDTO;
import com.ideality.coreflow.approval.query.dto.ProjectApprovalDTO;
import com.ideality.coreflow.approval.query.dto.ResponsePreviewApproval;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalMapper {
    List<ResponsePreviewApproval> selectMyApprovalReceive(long userId);

    List<ResponsePreviewApproval> selectApprovalByTaskId(long taskId);

    long selectApprovedApprovalById(long approvalId);

    List<ResponsePreviewApproval> selectMyApprovalSent(long userId);

    List<ApprovalParticipantDTO> selectApprovalParticipantById(long approvalId);

    ApprovalDetailsDTO selectApprovalDetailsById(long approvalId);

    List<ProjectApprovalDTO> selectProjectApprovalByProjectId(long projectId, ApprovalType approvalType);
}
