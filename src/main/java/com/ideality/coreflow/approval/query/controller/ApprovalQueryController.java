package com.ideality.coreflow.approval.query.controller;

import com.ideality.coreflow.approval.query.service.ApprovalQueryService;
import com.ideality.coreflow.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approval")
public class ApprovalQueryController {

    private final ApprovalQueryService approvalQueryService;

    // 수신한 결재 조회
    @GetMapping("/my-approval")
    public ResponseEntity<APIResponse<?>> searchMyApproval() {
        long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(APIResponse.success(approvalQueryService.searchMayApproval(id)));
    }
}
