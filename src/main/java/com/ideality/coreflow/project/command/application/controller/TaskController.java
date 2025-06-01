package com.ideality.coreflow.project.command.application.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {
    private final ProjectFacadeService projectFacadeService;

    @PostMapping("")
    public ResponseEntity<APIResponse<Long>> createTaskWithFacade
            (@RequestBody RequestTaskDTO requestTaskDTO) {
        Long taskId = projectFacadeService.createTask(requestTaskDTO);
        return ResponseEntity.ok(APIResponse.success(taskId, "태스크가 성공적으로 생성되었습니다."));
    }
}
