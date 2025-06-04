package com.ideality.coreflow.project.query.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dept")
public class DeptController {

    private final DeptQueryService deptQueryService;  // 필드 선언

    public DeptController(DeptQueryService deptQueryService) {
        this.deptQueryService = deptQueryService;
    }

    // 부서명 목록 조회
    @GetMapping("/all")
    public APIResponse<List<String>> getAllDeptNames() {  // 반환 타입을 APIResponse로 변경
        List<String> deptNames = deptQueryService.findAllDeptNames();
        return APIResponse.success(deptNames,"부서목록 조회 완료");  // 성공 응답 생성
    }
}