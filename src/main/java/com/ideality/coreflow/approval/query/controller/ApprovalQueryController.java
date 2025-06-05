package com.ideality.coreflow.approval.query.controller;

import com.ideality.coreflow.approval.query.service.ApprovalQueryService;
import com.ideality.coreflow.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approval")
public class ApprovalQueryController {

    private final ApprovalQueryService approvalQueryService;

    // 수신한 결재 조회
    @GetMapping("/my-approval/receive")
    public ResponseEntity<APIResponse<?>> searchMyApprovalReceive() {
        long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(APIResponse.success(approvalQueryService.searchMyApprovalReceive(id), "수신한 결재내역 조회"));
    }

    // workId로 결재 승인 이력 조회
    @GetMapping("/task-approval/{taskId}")
    public ResponseEntity<APIResponse<?>> searchApprovalByTaskId(@PathVariable long taskId) {
        return ResponseEntity.ok(APIResponse.success(approvalQueryService.searchApprovalByTaskId(taskId), "태스크 승인 이력 조회"));
    }

    // 발신 내역 조회
    @GetMapping("/my-approval/sent")
    public ResponseEntity<APIResponse<?>> searchMyApprovalSent() {
        long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(APIResponse.success(approvalQueryService.searchMyApprovalSent(id), "결재 발신 내역 조회"));
    }
}
