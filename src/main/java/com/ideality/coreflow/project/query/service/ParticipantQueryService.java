package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.command.application.dto.RequestInviteUserDTO;

import java.util.List;

public interface ParticipantQueryService {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);

    void findTeamLedaer(Long projectId, List<RequestInviteUserDTO> reqLeaderDTO);

    List<Long> selectParticipantsList(Long detailParticipantId);

    boolean isParticipant(Long userId, Long projectId);

    boolean isInviteRole(Long userId, Long projectId);

    void alreadyExistsMember(Long projectId, List<RequestInviteUserDTO> reqMemberDTO);

    List<Long> selectParticipantUserId(Long projectId);
}
