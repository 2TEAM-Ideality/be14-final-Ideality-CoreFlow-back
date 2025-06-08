package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.project.command.domain.repository.ProjectRepository;
import com.ideality.coreflow.project.query.dto.ProjectDetailResponseDTO;
import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;
import com.ideality.coreflow.project.query.dto.UserInfoDTO;
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
    public ProjectDetailResponseDTO getProjectDetail(Long projectId) {
        ProjectDetailResponseDTO projectDetail = projectMapper.getProjectDetail(projectId);
        List<UserInfoDTO> leaders = projectMapper.getProjectLeaders(projectId);
        ProjectDetailResponseDTO result = ProjectDetailResponseDTO.builder()
                .id(projectDetail.getId())
                .name(projectDetail.getName())
                .description(projectDetail.getDescription())
                .createdDate(projectDetail.getCreatedDate())
                .startBase(projectDetail.getStartBase())
                .endBase(projectDetail.getEndBase())
                .startExpect(projectDetail.getStartExpect())
                .endExpect(projectDetail.getEndExpect())
                .startReal(projectDetail.getStartReal())
                .endReal(projectDetail.getEndReal())
                .progressRate(projectDetail.getProgressRate())
                .passedRate(projectDetail.getPassedRate())
                .delayDays(projectDetail.getDelayDays())
                .status(projectDetail.getStatus())
                .director(projectDetail.getDirector())
                .leaders(leaders)
                .build();
        return result;
    }

    @Override
    public ProjectSummaryDTO selectProjectSummary(Long projectId) {
        return projectMapper.selectProjectSummary(projectId);
    }
}
