package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import org.apache.ibatis.javassist.NotFoundException;

public interface ProjectService {
    void existsById(Long projectId);

    Project createProject(ProjectCreateRequest request);

    boolean isCompleted(Long projectId);

    Long completeProject(Project project);

    Project findById(Long projectId) throws NotFoundException;

    Long updateProjectPending(Project project);

    Long updateProjectProgress(Project project);

    Long updateProjectDeleted(Project project);

    Long updateProjectCancelled(Project project);
}