package com.ideality.coreflow.approval.query.service;

import com.ideality.coreflow.approval.query.dto.ApprovalDetailsDTO;
import com.ideality.coreflow.approval.query.dto.ApprovalParticipantDTO;
import com.ideality.coreflow.approval.query.dto.ResponsePreviewApproval;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalQueryFacadeService {
    private final ApprovalQueryService approvalQueryService;

    public List<ResponsePreviewApproval> searchMyApprovalReceive(long id) {
        return approvalQueryService.searchMyApprovalReceive(id);
    }

    public List<ResponsePreviewApproval> searchMyApprovalSent(long id) {
        return approvalQueryService.searchMyApprovalSent(id);
    }

    public List<ResponsePreviewApproval> searchApprovalByTaskId(long taskId) {
        return approvalQueryService.searchApprovalByTaskId(taskId);
    }

    public ApprovalDetailsDTO searchApprovalDetailsById(long approvalId, long userId) {

        // 읽기 권한 설정
        // 참여자인지 확인 -> 상세 내역 조회 가능자인지 확인하는 로직

        // 결재 상세 내역 조회
        ApprovalDetailsDTO approvalDetails = approvalQueryService.searchApprovalDetailsById(approvalId);

        // 결재 참여자 조회
        List<ApprovalParticipantDTO> approvalParticipant = approvalQueryService.searchApprovalParticipantById(approvalId);

        List<String> hasRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        log.info("회원의 역할: {}", hasRole);



        return approvalDetails;
    }
}
