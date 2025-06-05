package com.ideality.coreflow.approval.query.mapper;

import com.ideality.coreflow.approval.query.dto.ApprovalDetailedDTO;
import com.ideality.coreflow.approval.query.dto.ResponseApprovalByTaskId;
import com.ideality.coreflow.approval.query.dto.ResponseApproval;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalMapper {
    List<ResponseApproval> selectMyApproval(long id);

    List<ResponseApprovalByTaskId> selectApprovalByTaskId(long taskId);

    ApprovalDetailedDTO selectApprovalById(long id);
}
