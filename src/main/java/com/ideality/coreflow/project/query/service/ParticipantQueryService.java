package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.command.application.dto.RequestTeamLeaderDTO;

import java.util.List;

import java.util.List;

public interface ParticipantQueryService {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);

    void findTeamLedaer(Long projectId, List<RequestTeamLeaderDTO> reqLeaderDTO);

    List<Long> selectParticipantsList(Long detailParticipantId);

    boolean isParticipant(Long userId, Long projectId);
}
