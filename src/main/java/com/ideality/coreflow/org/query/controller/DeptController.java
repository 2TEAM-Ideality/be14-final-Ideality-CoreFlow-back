package com.ideality.coreflow.org.query.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.query.dto.DepartmentDTO;
import com.ideality.coreflow.org.query.service.DeptQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public APIResponse<List<DepartmentDTO>> getAllDeptNames() {
        // 부서명과 부서 ID를 포함한 DTO 리스트를 가져옴
        List<DepartmentDTO> deptList = deptQueryService.findAllDeptNames();

        // 성공 응답을 생성하여 반환
        return APIResponse.success(deptList, "부서목록 조회 완료");
    }

    // 태스크에 참여 중인 부서 조회
    @GetMapping("/task/{workId}")
    public ResponseEntity<APIResponse<?>> getDeptListByTaskId(@PathVariable("workId") Long workId) {
        List<DepartmentDTO> deptList = deptQueryService.findDeptListByTaskId(workId);
        return ResponseEntity.ok(APIResponse.success(deptList, "부서 목록 조회 완료"));
    }
}