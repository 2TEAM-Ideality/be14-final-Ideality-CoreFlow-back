package com.ideality.coreflow.org.command.application.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.org.command.application.service.OrgFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/org")
public class OrgController {

    private final OrgFacadeService orgFacadeService;

    // 추후 부서도 옮겨올 예정

    // 직위 조회
    @GetMapping("/job-rank/search")
    public ResponseEntity<APIResponse<?>> searchAllJobRank() {
        return ResponseEntity.ok(APIResponse.success(orgFacadeService.searchAllJobRank(), "모든 직위 조회"));
    }

    // 직책

}
