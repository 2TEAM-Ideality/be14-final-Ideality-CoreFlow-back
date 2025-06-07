package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.query.dto.ProjectDetailResponseDTO;
import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;
import java.util.List;

public interface ProjectQueryService {
    List<ProjectSummaryDTO> selectProjectSummaries(Long userId);

    ProjectDetailResponseDTO getProjectDetail(Long projectId);
}
