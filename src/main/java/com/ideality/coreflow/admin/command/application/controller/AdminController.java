package com.ideality.coreflow.admin.command.application.controller;

import com.ideality.coreflow.admin.command.application.dto.*;
import com.ideality.coreflow.admin.command.application.service.AdminFacadeService;
import com.ideality.coreflow.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(("/api/admin"))
public class AdminController {

    private final AdminFacadeService adminFacadeService;

    @PatchMapping("/user/{userId}")
    public ResponseEntity<APIResponse<?>> modifyUserInfoByAdmin(@PathVariable Long userId,
                                                                @RequestBody RequestUserUpdateByAdmin requestUserUpdateByAdmin) {

        adminFacadeService.modifyUserInfoByAdmin(userId, requestUserUpdateByAdmin);

        return ResponseEntity.ok(APIResponse.success(null, "유저 정보 수정 완료"));
    }

    // 직위 추가
    @PostMapping("/job-rank/regist")
    public ResponseEntity<APIResponse<?>> registJobRank(@RequestBody RequestRegistJobRank request) {
        adminFacadeService.registJobRank(request.getName());
        return ResponseEntity.ok(APIResponse.success(null, "직위 추가 완료"));
    }

    // 직위 수정
    @PatchMapping("/job-rank/modify")
    public ResponseEntity<APIResponse<?>> modifyJobRank(@RequestBody RequestModifyJobRank request) {
        adminFacadeService.modifyJobRank(request);
        return ResponseEntity.ok(APIResponse.success(null, "직위 수정 완료"));
    }

    // 직위 삭제
    @DeleteMapping("/job-rank/delete")
    public ResponseEntity<APIResponse<?>> deleteJobRank(@RequestParam long id) {
        adminFacadeService.deleteJobRank(id);
        return ResponseEntity.ok(APIResponse.success(null, "직위 삭제 완료"));
    }

    // 직책 추가
    @PostMapping("/job-role/regist")
    public ResponseEntity<APIResponse<?>> registJobRole(@RequestBody RequestRegistJobRole request) {
        adminFacadeService.registJobRole(request.getName());
        return ResponseEntity.ok(APIResponse.success(null, "직책 추가 완료"));
    }

    // 직책 수정
    @PatchMapping("/job-role/modify")
    public ResponseEntity<APIResponse<?>> modifyJobRole(@RequestBody RequestModifyJobROle request) {
        adminFacadeService.modifyJobRole(request);
        return ResponseEntity.ok(APIResponse.success(null, "직책 수정 완료"));
    }

    // 직책 삭제
    @DeleteMapping("/job-role/delete")
    public ResponseEntity<APIResponse<?>> deleteJobRole(@RequestParam long id) {
        adminFacadeService.deleteJobRole(id);
        return ResponseEntity.ok(APIResponse.success(null, "직책 삭제 완료"));
    }
}
