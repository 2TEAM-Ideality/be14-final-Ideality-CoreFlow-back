package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.query.dto.ProjectDetailResponseDTO;
import com.ideality.coreflow.project.query.dto.PipelineResponseDTO;
import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;
import com.ideality.coreflow.project.query.dto.TaskProgressDTO;
import java.util.List;

public interface ProjectQueryService {
    List<ProjectSummaryDTO> selectProjectSummaries(Long userId);

    ProjectSummaryDTO selectProjectSummary(Long projectId);

    ProjectDetailResponseDTO getProjectDetail(Long projectId);

    PipelineResponseDTO getPipeline(Long projectId);

    String getProjectName(Long projectId);
}
