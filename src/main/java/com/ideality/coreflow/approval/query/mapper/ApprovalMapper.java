package com.ideality.coreflow.approval.query.mapper;

import com.ideality.coreflow.approval.query.dto.ResponseApprovalByTaskId;
import com.ideality.coreflow.approval.query.dto.ResponseQueryApproval;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalMapper {
    List<ResponseQueryApproval> selectMyApproval(long id);

    List<ResponseApprovalByTaskId> selectApprovalByTaskId(long taskId);
}
