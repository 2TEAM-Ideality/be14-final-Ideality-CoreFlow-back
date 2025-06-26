package com.ideality.coreflow.tenant.command.application.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.tenant.command.application.dto.RequestCreateTenant;
import com.ideality.coreflow.tenant.command.application.service.TenantFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {
    private final TenantFacadeService tenantFacadeService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse<?>> createTenant(@RequestBody RequestCreateTenant request) {
        String schemaName = request.getSchemaName();
        log.info("schemaName={}", schemaName);
        return ResponseEntity.ok(APIResponse.success(tenantFacadeService.createNewTenant(request), schemaName + " 테넌트 생성 완료"));
    }

    @PostMapping("/delete/{tenantId}")
    public ResponseEntity<APIResponse<?>> deleteTenant(@PathVariable("tenantId") Long tenantId) {
        tenantFacadeService.deleteTenant(tenantId);
        return ResponseEntity.ok(APIResponse.success(null, "테넌트 삭제 완료"));
    }
}
