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
}
