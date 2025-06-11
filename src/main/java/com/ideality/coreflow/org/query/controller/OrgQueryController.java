package com.ideality.coreflow.org.query.controller;

import com.ideality.coreflow.org.query.service.OrgQueryFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/org")
public class OrgQueryController {

    private final OrgQueryFacadeService orgQueryFacadeService;


}
