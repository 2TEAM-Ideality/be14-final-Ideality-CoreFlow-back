package com.ideality.coreflow.project.query.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;

@Mapper
public interface ParticipantMapper {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);

    List<Long> selectParticipantsList(Long detailParticipantId);

    boolean isParticipantUser(Long userId, Long projectId);

    List<ParticipantDTO> selectProjectParticipantList(Long projectId);
}
