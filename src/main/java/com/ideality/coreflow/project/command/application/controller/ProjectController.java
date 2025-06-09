package com.ideality.coreflow.project.command.application.controller;

import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PatchMapping("/complete/{projectId}")
    public ResponseEntity<Map<String, Object>> complteProject(@PathVariable Long projectId)
            throws NotFoundException, IllegalAccessException {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Long completedProjectId = projectFacadeService.updateProjectComplete(projectId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", completedProjectId+"번 프로젝트 완료됨");
        response.put("data", completedProjectId);
        return ResponseEntity.ok(response);
    }


    // TODO. 프로젝트 리포트 생성
//    @PostMapping("/report/download")


}