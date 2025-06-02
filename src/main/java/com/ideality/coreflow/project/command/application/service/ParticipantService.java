package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;

import java.util.List;

public interface ParticipantService {
    void createParticipants(List<ParticipantDTO> participants);


    void updateTeamLeader(Long leaderId, Long taskId, TargetType targetType);
}
