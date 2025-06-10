package com.ideality.coreflow.project.query.service;

import java.util.List;

import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;

import java.util.List;

public interface ParticipantQueryService {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);

	List<ParticipantDTO> getProjectParticipantList(Long projectId);

    List<Long> selectParticipantsList(Long detailParticipantId);

    boolean isParticipant(Long userId, Long projectId);
}
