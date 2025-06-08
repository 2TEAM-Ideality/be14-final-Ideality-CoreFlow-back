package com.ideality.coreflow.approval.command.application.controller;

import com.ideality.coreflow.approval.command.application.dto.RequestApproval;
import com.ideality.coreflow.approval.command.application.dto.RequestApprove;
import com.ideality.coreflow.approval.command.application.dto.RequestReject;
import com.ideality.coreflow.approval.command.application.service.ApprovalFacadeService;
import com.ideality.coreflow.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/approval")
public class ApprovalController {

    private final ApprovalFacadeService approvalFacadeService;

    // 결재 승인
    @PatchMapping("/approve")
    public ResponseEntity<APIResponse<?>> approveApproval(@RequestBody RequestApprove request) {

        approvalFacadeService.approve(request, Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName()));
        return ResponseEntity.ok(APIResponse.success(null, "승인 완료"));
    }

    // 결재 반려
    @PatchMapping("/reject")
    public ResponseEntity<APIResponse<?>> rejectApproval(@RequestBody RequestReject request) {

        approvalFacadeService.reject(request, Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName()));
        return ResponseEntity.ok(APIResponse.success(request.getReason(), "반려 완료"));
    }

    // 결재 요청
    @PostMapping("/request")
    public ResponseEntity<APIResponse<?>> requestApproval(@RequestBody RequestApproval request) {
        // request: 결재 정보
        long requesterId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        return ResponseEntity.ok(APIResponse.success(approvalFacadeService.requestApproval(request, requesterId), "결재 요청 완료"));
    }

    // 첨부파일 업로드
    @PostMapping(value = "/request/{approvalId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<?>> approvalAttachmentUpload(@PathVariable long approvalId, @RequestParam("file") MultipartFile file ) {

        long uploaderId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        approvalFacadeService.uploadApprovalAttachment(approvalId, file, uploaderId);

        return ResponseEntity.ok(APIResponse.success(null, "첨부파일 등록 완료"));
    }
}
