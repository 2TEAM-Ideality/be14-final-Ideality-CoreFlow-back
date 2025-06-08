package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.project.command.domain.repository.ProjectRepository;
import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;
import com.ideality.coreflow.project.query.mapper.ProjectMapper;
import com.ideality.coreflow.project.query.service.ProjectQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectQueryServiceImpl implements ProjectQueryService {

    private final ProjectMapper projectMapper;

    @Override
    public List<ProjectSummaryDTO> selectProjectSummaries(Long userId) {
        return projectMapper.selectParticipatingProjects(userId);
    }

    @Override
    public ProjectSummaryDTO selectProjectSummary(Long projectId) {
        return projectMapper.selectProjectSummary(projectId);
    }
}
