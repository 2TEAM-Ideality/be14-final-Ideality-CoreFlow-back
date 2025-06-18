package com.ideality.coreflow.tenant.command.application.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.tenant.command.application.dto.RequestCreateTenant;
import com.ideality.coreflow.tenant.command.application.service.TenantFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(APIResponse.success(tenantFacadeService.createNewTenant(request), "테넌트 생성" + schemaName));
    }
}
