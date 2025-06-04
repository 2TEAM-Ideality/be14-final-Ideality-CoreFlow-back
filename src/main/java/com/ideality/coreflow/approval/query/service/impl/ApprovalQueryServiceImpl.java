package com.ideality.coreflow.approval.query.service.impl;

import com.ideality.coreflow.approval.query.dto.ResponseQueryApproval;
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
    public List<ResponseQueryApproval> searchMayApproval(long id) {

        return approvalMapper.selectMyApproval(id);
    }
}
