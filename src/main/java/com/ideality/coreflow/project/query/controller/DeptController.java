package com.ideality.coreflow.project.query.controller;

import com.ideality.coreflow.project.query.service.DeptQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dept")
public class DeptController {

    private final DeptQueryService deptQueryService;  // 필드 선언

    public DeptController(DeptQueryService deptQueryService) {
        this.deptQueryService = deptQueryService;
    }

    // 부서명 목록 조회
    @GetMapping("/all")
    public List<String> getAllDeptNames() {
        return deptQueryService.findAllDeptNames();
    }
}