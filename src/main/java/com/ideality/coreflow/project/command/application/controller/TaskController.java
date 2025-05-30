package com.ideality.coreflow.project.command.application.controller;

import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.service.ProjectFacadeService;
import lombok.RequiredArgsConstructor;
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
    public Long createTaskWithFacade(@RequestBody RequestTaskDTO requestTaskDTO) {
        return  projectFacadeService.createTask(requestTaskDTO);
    }
}
