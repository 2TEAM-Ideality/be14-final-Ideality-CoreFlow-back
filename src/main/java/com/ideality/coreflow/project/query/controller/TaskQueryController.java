package com.ideality.coreflow.project.query.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.query.dto.*;
import com.ideality.coreflow.project.query.service.facade.ProjectQueryFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskQueryController {
    private final ProjectQueryFacadeService projectQueryFacadeService;

    @GetMapping("/detail/{taskId}")
    public ResponseEntity<APIResponse<ResponseTaskInfoDTO>> getTaskInfo (@PathVariable Long taskId) {

        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        ResponseTaskInfoDTO selectTask = projectQueryFacadeService.selectTaskInfo(taskId, userId);
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

    // 완료된 태스크 목록 조회
    @GetMapping("/completed/{projectId}")
    public ResponseEntity<APIResponse<List<CompletedTaskDTO>>> getCompletedTasks(@PathVariable Long projectId) {
        List<CompletedTaskDTO> completedTasks = projectQueryFacadeService.selectCompletedTasks(projectId);
        return ResponseEntity.ok(APIResponse.success(completedTasks, "완료된 태스크 목록 조회 성공"));
    }

    // 태스크별 참여자 조회
    @GetMapping("/{taskId}/participant")
    public ResponseEntity<APIResponse<List<ParticipantUserDTO>>> getTaskParticipant(@PathVariable Long taskId) {
        List<ParticipantUserDTO> taskParticipants = projectQueryFacadeService.getTaskParticipant(taskId);
        return ResponseEntity.ok(APIResponse.success(taskParticipants, "태스크별 참여자 목록 조회 성공"));
    }


    // 부서 일정 조회
    @GetMapping("/dept")
    public ResponseEntity<APIResponse<List<DeptWorkDTO>>> getTasksByDept (@RequestParam Long userId) {

        List<DeptWorkDTO> response = projectQueryFacadeService.selectWorksByDeptId(userId);
        	return ResponseEntity.ok(APIResponse.success(response, "부서 일정 목록 조회 성공"));
        //
    }

    // 오늘의 부서 일정 조회
    @GetMapping("/dept/today")
    public ResponseEntity<APIResponse<List<DeptWorkDTO>>> getDeptTaskToday(@RequestParam Long userId) {
        List<DeptWorkDTO> response = projectQueryFacadeService.selectTodayWorksByDeptId(userId);
        return ResponseEntity.ok(APIResponse.success(response, "부서 일정 목록 조회 성공"));
    }

    // warning의 경우 몇일 늦는지
    @GetMapping("/{taskId}/warningDate")
    public ResponseEntity<APIResponse<?>> getTaskWarningDate(@PathVariable Long taskId) {
        return ResponseEntity.ok(APIResponse.success(projectQueryFacadeService.getTaskWarningDate(taskId), "지연 예상일 조회"));
    }
}
