package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.domain.aggregate.Participant;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;

import java.util.List;

public interface ParticipantService {
    void createParticipants(List<ParticipantDTO> taskParticipants);
    void createAssignee(ParticipantDTO assigneeDTO);
}
