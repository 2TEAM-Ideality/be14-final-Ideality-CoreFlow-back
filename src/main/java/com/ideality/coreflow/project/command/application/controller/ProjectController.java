package com.ideality.coreflow.project.command.application.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/project")
@RestController
public class ProjectController {

    //    private final ProjectService projectService;
    private final ProjectFacadeService projectFacadeService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProject(@RequestBody ProjectCreateRequest request) {
        Project result = projectFacadeService.createProject(request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", result.getId()+"번 프로젝트 생성 완료");
        response.put("data", result);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/status/{status}")
    public ResponseEntity<Map<String, Object>> updateProjectStatus(@PathVariable Long projectId,
                                                                   @PathVariable String status)
            throws NotFoundException, IllegalAccessException {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        Status targetStatus;
        try {
            targetStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 상태값입니다: " + status);
        }

        Long updatedProjectId = projectFacadeService.updateProjectStatus(projectId, userId, targetStatus);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", updatedProjectId + "번 프로젝트 상태가 '" + targetStatus + "'로 변경되었습니다");
        response.put("data", updatedProjectId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/passed-rate")
    public ResponseEntity<APIResponse<Map<String,Object>>> updateProjectPassedRate(@PathVariable Long projectId){
        Double updatedPassedRate = projectFacadeService.updateProjectPassedRate(projectId);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("updatedPassedRate", updatedPassedRate),
                        projectId + "번 프로젝트의 진행률이 업데이트 되었습니다")
        );
    }
}