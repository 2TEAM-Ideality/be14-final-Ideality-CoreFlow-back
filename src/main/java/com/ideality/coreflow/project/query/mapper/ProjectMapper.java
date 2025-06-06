package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper {
    List<ProjectSummaryDTO> selectParticipatingProjects(Long userId);
}
