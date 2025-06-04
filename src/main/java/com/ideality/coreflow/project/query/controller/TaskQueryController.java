package com.ideality.coreflow.project.query.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.project.query.service.facade.ProjectQueryFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskQueryController {
    private final ProjectQueryFacadeService projectQueryFacadeService;

    @GetMapping("/{taskId}")
    public ResponseEntity<APIResponse<ResponseTaskInfoDTO>> getTaskInfo (@PathVariable Long taskId) {

        ResponseTaskInfoDTO selectTask = projectQueryFacadeService.selectTaskInfo(taskId);
        return ResponseEntity.ok(
                APIResponse.success(selectTask, "태스크 상세 정보 조회 성공")
        );
    }
}
