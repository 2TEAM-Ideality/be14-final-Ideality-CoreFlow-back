package com.ideality.coreflow.approval.command.application.controller;

import com.ideality.coreflow.approval.command.application.dto.RequestApproval;
import com.ideality.coreflow.approval.command.application.service.ApprovalFacadeService;
import com.ideality.coreflow.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approval")
public class ApprovalController {

    private final ApprovalFacadeService approvalFacadeService;

    @PatchMapping("/approve")
    public ResponseEntity<APIResponse<?>> approve(@RequestBody RequestApproval request) {

        approvalFacadeService.approve(request, Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName()));
        return ResponseEntity.ok(APIResponse.success(null, "승인 완료"));
    }
}
