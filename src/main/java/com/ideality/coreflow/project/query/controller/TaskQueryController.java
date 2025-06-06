package com.ideality.coreflow.project.query.controller;

import com.ideality.coreflow.calendar.query.dto.RequestUserDTO;
import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.query.dto.DeptWorkDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.project.query.service.facade.ProjectQueryFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskQueryController {
    private final ProjectQueryFacadeService projectQueryFacadeService;

    @GetMapping("/detail/{taskId}")
    public ResponseEntity<APIResponse<ResponseTaskInfoDTO>> getTaskInfo (@PathVariable Long taskId) {

        ResponseTaskInfoDTO selectTask = projectQueryFacadeService.selectTaskInfo(taskId);
        return ResponseEntity.ok(
                APIResponse.success(selectTask, "태스크 상세 정보 조회 성공")
        );
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<APIResponse<List<ResponseTaskDTO>>> getTasks (@PathVariable Long projectId) {

        List<ResponseTaskDTO> tasks = projectQueryFacadeService.selectTasks(projectId);
        return ResponseEntity.ok(
                APIResponse.success(tasks, "태스크 목록 조회 성공")
        );
    }

    @GetMapping("/dept")
    public ResponseEntity<APIResponse<List<DeptWorkDTO>>> getTasksByDept (@RequestBody RequestUserDTO requestDTO) {

        List<DeptWorkDTO> response = projectQueryFacadeService.selectWorksByDeptId(requestDTO.getUserId());
        	return ResponseEntity.ok(APIResponse.success(response, "부서 일정 목록 조회 성공"));
        //
    }

}
