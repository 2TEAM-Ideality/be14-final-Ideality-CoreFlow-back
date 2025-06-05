package com.ideality.coreflow.approval.query.mapper;

import com.ideality.coreflow.approval.query.dto.ApprovalDetailedDTO;
import com.ideality.coreflow.approval.query.dto.ResponsePreviewApproval;
import com.ideality.coreflow.approval.query.dto.ResponseApproval;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalMapper {
    List<ResponsePreviewApproval> selectMyApprovalReceive(long id);

    List<ResponsePreviewApproval> selectApprovalByTaskId(long taskId);

    ApprovalDetailedDTO selectApprovedApprovalById(long id);

    List<ResponsePreviewApproval> selectMyApprovalSent(long id);
}
