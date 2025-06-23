package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.query.dto.*;

import java.util.List;
import org.apache.ibatis.javassist.NotFoundException;

public interface ProjectService {
    void existsById(Long projectId);

    Project createProject(ProjectCreateRequest request);

    boolean isCompleted(Long projectId);

    Project findById(Long projectId);

    Long updateProjectStatus(Project project, Status targetStatus);

    Double updateProjectPassedRate(Long projectId);

    Double updateProjectProgress(Long projectId);

    List<ProjectOTD> calculateProjectOTD(List<CompletedProjectDTO> completedProjectList);

    ProjectDateDTO findProjectDateById(Long projectId);

    void projectSave(Project project);
}