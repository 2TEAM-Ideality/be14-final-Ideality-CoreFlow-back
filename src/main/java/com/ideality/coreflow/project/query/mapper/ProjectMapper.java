package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.ProjectDetailResponseDTO;
import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;
import com.ideality.coreflow.project.query.dto.UserInfoDTO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProjectMapper {
    List<ProjectSummaryDTO> selectParticipatingProjects(Long userId);
    ProjectDetailResponseDTO getProjectDetail(Long projectId);
    List<UserInfoDTO> getProjectLeaders(Long projectId);

    ProjectSummaryDTO selectProjectSummary(Long projectId);
    String findProjectNameByProjectId(Long projectId);
}
