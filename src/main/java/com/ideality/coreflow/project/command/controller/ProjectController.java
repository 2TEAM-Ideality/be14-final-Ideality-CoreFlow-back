package com.ideality.coreflow.project.command.controller;

import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.service.ProjectService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/project")
@RestController
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProject(@RequestBody ProjectCreateRequest request) {
        Project result = projectService.createProject(request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", result.getId()+"번 프로젝트 생성 완료");
        response.put("data", result);
        return ResponseEntity.ok(response);
    }
}
