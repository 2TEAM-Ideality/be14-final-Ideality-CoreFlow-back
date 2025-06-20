package com.ideality.coreflow.org.query.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.org.query.service.OrgQueryFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/org")
public class OrgQueryController {

    private final OrgQueryFacadeService orgQueryFacadeService;

    // 부서 조회
    @GetMapping("/dept/search")
    public ResponseEntity<APIResponse<?>> searchAllDept() {
        return ResponseEntity.ok(APIResponse.success(orgQueryFacadeService.searchAllDept(), "모든 부서 조회"));
    }

    // 직위 조회
    @GetMapping("/job-rank/search")
    public ResponseEntity<APIResponse<?>> searchAllJobRank() {
        return ResponseEntity.ok(APIResponse.success(orgQueryFacadeService.searchAllJobRank(), "모든 직위 조회"));
    }

    // 직책 조회
    @GetMapping("/job-role/search")
    public ResponseEntity<APIResponse<?>> searchAllJobRole() {
        return ResponseEntity.ok(APIResponse.success(orgQueryFacadeService.searchAllJobRole(), "모든 직책 조회"));
    }

    @GetMapping("/all/info")
    public ResponseEntity<APIResponse<?>> searchAllOrgInfo() {
        return ResponseEntity.ok(APIResponse.success(orgQueryFacadeService.searchAllOrgInfo(), "모든 조직 정보 조회"));
    }

}
