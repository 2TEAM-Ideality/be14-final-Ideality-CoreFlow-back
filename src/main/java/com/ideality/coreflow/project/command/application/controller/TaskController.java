package com.ideality.coreflow.project.command.application.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {
    private final ProjectFacadeService projectFacadeService;
    private final TaskService taskService;

    @PostMapping("")
    public ResponseEntity<APIResponse<Map<String, Long>>> createTaskWithFacade(
            @RequestBody RequestTaskDTO requestTaskDTO) {
        Long taskId = projectFacadeService.createTask(requestTaskDTO);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("taskId", taskId),
                        "태스크가 성공적으로 생성되었습니다.")
        );
    }

    @PatchMapping("/progress/{taskId}")
    public ResponseEntity<APIResponse<Map<String, Long>>> updateTaskByProgress(
            @PathVariable Long taskId) {
        Long updatedTaskId = projectFacadeService.updateStatusProgress(taskId);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("taskId", updatedTaskId),
                        "태스크 상태가 진행 상태로 변경되었습니다.")
        );
    }

    @PatchMapping("/complete/{taskId}")
    public ResponseEntity<APIResponse<Map<String, Long>>> updateTaskByComplete(
            @PathVariable Long taskId) {
        Long updatedTaskId = projectFacadeService.updateStatusComplete(taskId);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("taskId", updatedTaskId),
                        "태스크가 완료 상태로 변경되었습니다.")
        );
    }

    @PatchMapping("/delete/{taskId}")
    public ResponseEntity<APIResponse<Map<String, Long>>> softDeleteTask(
            @PathVariable Long taskId
    ) {
        Long deleteTaskId = projectFacadeService.deleteTaskBySoft(taskId);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("taskId", deleteTaskId),
                        "태스크가 삭제 되었습니다.")
        );
    }

    @PatchMapping("/{taskId}/passed-rate")
    public ResponseEntity<APIResponse<Map<String, Double>>> updateTaskPassedRate(@PathVariable Long taskId) {
        Double passedRate = projectFacadeService.updatePassedRate(taskId);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("passedRate", passedRate),
                        taskId + " 번 태스크의 경과율이 업데이트 되었습니다.")
        );
    }

    @PatchMapping("/{taskId}/progress-rate")
    public ResponseEntity<APIResponse<Map<String,Object>>> updateTaskProgressRate(@PathVariable Long taskId) {
        Double progressRate = projectFacadeService.updateProgressRate(taskId);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("progressRate", progressRate),
                        taskId + "번 태스크의 진척률이 업데이트 되었습니다.")
        );
    }
}
