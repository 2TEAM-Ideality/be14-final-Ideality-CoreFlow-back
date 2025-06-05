package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;

public interface ProjectService {
    void existsById(Long projectId);
    Project createProject(ProjectCreateRequest request);

    boolean isCompleted(Long projectId);
}