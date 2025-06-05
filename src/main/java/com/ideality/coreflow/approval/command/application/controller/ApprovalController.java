package com.ideality.coreflow.approval.command.application.controller;

import com.ideality.coreflow.approval.command.application.dto.RequestApprove;
import com.ideality.coreflow.approval.command.application.dto.RequestReject;
import com.ideality.coreflow.approval.command.application.service.ApprovalFacadeService;
import com.ideality.coreflow.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approval")
public class ApprovalController {

    private final ApprovalFacadeService approvalFacadeService;

    // 결재 승인
    @PatchMapping("/approve")
    public ResponseEntity<APIResponse<?>> approve(@RequestBody RequestApprove request) {

        approvalFacadeService.approve(request, Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName()));
        return ResponseEntity.ok(APIResponse.success(null, "승인 완료"));
    }

    // 결재 반려
    @PatchMapping("/reject")
    public ResponseEntity<APIResponse<?>> reject(@RequestBody RequestReject request) {

        approvalFacadeService.reject(request, Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName()));
        return ResponseEntity.ok(APIResponse.success(request.getReason(), "반려 완료"));
    }
}
