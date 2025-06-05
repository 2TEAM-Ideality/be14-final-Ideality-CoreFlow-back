package com.ideality.coreflow.project.query.controller;

import com.ideality.coreflow.calendar.query.dto.RequestUserDTO;
import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.query.dto.DeptWorkDTO;
import com.ideality.coreflow.project.query.dto.DetailDTO;
import com.ideality.coreflow.project.query.dto.WorkDetailDTO;
import com.ideality.coreflow.project.query.service.WorkService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work")
public class WorkController {

    private final WorkService workService;

    // 태스크 내의 세부일정 이름 목록 조회
    @GetMapping("/detail/nameList")
    public APIResponse<List<String>> getSubTaskNames(@RequestParam Long parentTaskId) {

        // 후행 일정 이름 목록을 조회
        List<String> subTaskNames = workService.getSubTaskNamesByParentTaskId(parentTaskId);

        // 성공 응답을 APIResponse를 사용하여 반환
        return APIResponse.success(subTaskNames, "선행/후행 일정을 조회했습니다.");
    }

    @GetMapping("/detailList")
    public APIResponse<List<DetailDTO>> getSubTaskDetails(@RequestParam Long parentTaskId) {
        // 세부 일정과 담당 부서 정보를 조회
        List<DetailDTO> subTaskDetails = workService.getSubTaskDetailsByParentTaskId(parentTaskId);

        // 응답을 APIResponse로 감싸서 반환
        return APIResponse.success(subTaskDetails, "세부 일정과 담당 부서 목록 조회 성공");
    }

    @GetMapping("/detail")
    public APIResponse<WorkDetailDTO> getWorkDetail(@RequestParam Long workId) {
        // 세부 일정 상세 정보를 조회
        WorkDetailDTO workDetail = workService.getWorkDetailById(workId);

        // 응답을 APIResponse로 감싸서 반환
        return APIResponse.success(workDetail, "세부 일정 조회 성공");
    }

}
