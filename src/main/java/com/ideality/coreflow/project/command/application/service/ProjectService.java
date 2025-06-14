package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.query.dto.CompletedProjectDTO;
import com.ideality.coreflow.project.query.dto.ProjectOTD;
import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;
import com.ideality.coreflow.project.query.dto.TaskProgressDTO;
import java.util.List;
import org.apache.ibatis.javassist.NotFoundException;

public interface ProjectService {
    void existsById(Long projectId);

    Project createProject(ProjectCreateRequest request);

    boolean isCompleted(Long projectId);

    Project findById(Long projectId) throws NotFoundException;

    Long updateProjectStatus(Project project, Status targetStatus);

    Double updateProjectPassedRate(Long projectId);

    Double updateProjectProgress(Long projectId, List<TaskProgressDTO> taskList);

    List<ProjectOTD> calculateProjectOTD(List<CompletedProjectDTO> completedProjectList);
    Double updateProjectProgress(Long projectId);
}