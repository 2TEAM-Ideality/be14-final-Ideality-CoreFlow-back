package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.query.dto.CompletedProjectDTO;
import com.ideality.coreflow.project.query.dto.ProjectDetailResponseDTO;
import com.ideality.coreflow.project.query.dto.PipelineResponseDTO;
import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;

import java.util.List;

public interface ProjectQueryService {
    List<ProjectSummaryDTO> selectProjectSummaries(Long userId);

    ProjectDetailResponseDTO getProjectDetail(Long projectId);

    PipelineResponseDTO getPipeline(Long projectId);

    List<CompletedProjectDTO> selectCompletedProjects();

    ProjectSummaryDTO selectProjectSummary(Long projectId);

    String getProjectName(Long projectId);

	List<ProjectSummaryDTO> selectProjectByDeptId(Long deptId);

    String findProjectNameByProjectId(Long projectId);

    List<Long> getProjectIdsInProgressByUser(Long userId);
}
