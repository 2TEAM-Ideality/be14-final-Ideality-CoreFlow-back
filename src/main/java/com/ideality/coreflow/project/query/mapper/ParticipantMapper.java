package com.ideality.coreflow.project.query.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.query.dto.report.ProjectParticipantDTO;

@Mapper
public interface ParticipantMapper {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);

    List<Long> selectParticipantsList(Long detailParticipantId);

    boolean isParticipantUser(Long userId, Long projectId);

    List<ProjectParticipantDTO> selectProjectParticipantList(Long projectId);
}
