package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.DateInfoDTO;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.query.dto.*;

import java.util.List;

public interface ProjectService {
    void existsById(Long projectId);

    Project createProject(ProjectCreateRequest request);

    boolean isCompleted(Long projectId);

    Project findById(Long projectId);

    Long updateProjectStatus(Long projectId, Status targetStatus);

    Double updateProjectPassedRate(Long projectId, Double passedRate);

    Double updateProjectProgress(Long projectId, Double progress);

    ProjectDateDTO findProjectDateById(Long projectId);

    void projectSave(Project project);

    DateInfoDTO findDateInfoByProjectId(Long projectId);

    List<Project> findAllByStatusNotIn(List<Status> statuses);

}