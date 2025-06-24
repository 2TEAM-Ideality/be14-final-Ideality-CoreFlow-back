package com.ideality.coreflow.project.command.application.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.approval.command.application.dto.DelayInfoDTO;
import com.ideality.coreflow.project.command.application.dto.RequestModifyTaskDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.command.domain.service.DelayDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final ProjectFacadeService projectFacadeService;
    private final DelayDomainService delayDomainService;    // 추후 삭제

    @PostMapping("")
    public ResponseEntity<APIResponse<Map<String, Long>>> createTaskWithFacade(
            @RequestBody RequestTaskDTO requestTaskDTO) {
        log.info("태스크 생성 요청 들어옴");
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Long taskId = projectFacadeService.createTask(requestTaskDTO, userId);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("taskId", taskId),
                        "태스크가 성공적으로 생성되었습니다.")
        );
    }

    @PatchMapping("/progress/{taskId}")
    public ResponseEntity<APIResponse<Map<String, Long>>> updateTaskByProgress(
            @PathVariable Long taskId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Long updatedTaskId = projectFacadeService.updateStatusProgress(taskId, userId);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("taskId", updatedTaskId),
                        "태스크 상태가 진행 상태로 변경되었습니다.")
        );
    }

    @PatchMapping("/complete/{taskId}")
    public ResponseEntity<APIResponse<Map<String, Long>>> updateTaskByComplete(
            @PathVariable Long taskId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Long updatedTaskId = projectFacadeService.updateStatusComplete(taskId, userId);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("taskId", updatedTaskId),
                        "태스크가 완료 상태로 변경되었습니다.")
        );
    }

    @PatchMapping("/delete/{taskId}")
    public ResponseEntity<APIResponse<Map<String, Long>>> softDeleteTask(
            @PathVariable Long taskId
    ) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Long deleteTaskId = projectFacadeService.deleteTaskBySoft(taskId, userId);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("taskId", deleteTaskId),
                        "태스크가 삭제 되었습니다.")
        );
    }

    @PatchMapping("/{taskId}/passed-rate")
    public ResponseEntity<APIResponse<Map<String, Double>>> updateTaskPassedRate(@PathVariable Long taskId) {
        Double passedRate = projectFacadeService.updatePassedRate(taskId, TargetType.TASK);
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

    // test용 나중에 삭제 필요
    @PatchMapping("{taskId}/delay")
    public ResponseEntity<APIResponse<?>> delayTaskPropagate(@PathVariable Long taskId, @RequestParam Integer delayDays) {
        DelayInfoDTO response = delayDomainService.delayAndPropagateLogic(taskId, delayDays, false);
        return ResponseEntity.ok(
                APIResponse.success(response,
                        taskId + "번 태스크를 " + delayDays + "일 지연시킨 결과 " + response.getTaskCountByDelay() + "개의 태스크가 지연되었습니다.")
        );
    }

    @PatchMapping("/modify/{taskId}")
    public ResponseEntity<APIResponse<Map<String, Long>>>
        updateTaskDetail(@PathVariable Long taskId, @RequestBody RequestModifyTaskDTO requestModifyTaskDTO) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        Long modifyTaskId = projectFacadeService.updateTaskDetail(requestModifyTaskDTO, userId, taskId);
        return ResponseEntity.ok(APIResponse.success(Map.of("taskId", modifyTaskId),
                taskId + "가 업데이트 되었습니다.")
        );
    }
}
