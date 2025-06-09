package com.ideality.coreflow.project.query.service;

import java.util.List;

import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;

public interface ParticipantQueryService {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);

	List<ParticipantDTO> getParticipantList(Long projectId);
}
