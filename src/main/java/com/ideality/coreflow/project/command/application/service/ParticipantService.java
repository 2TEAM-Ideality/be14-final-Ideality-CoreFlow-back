package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;

import java.util.List;

public interface ParticipantService {
    void createParticipants(List<ParticipantDTO> taskParticipants);
    void createAssignee(ParticipantDTO assigneeDTO);
    void updateAssignee(Long taskId, ParticipantDTO assigneeDTO);
    void updateParticipants(Long taskId, List<Long> participantIds);

    boolean isParticipant(long targetId, long userId, TargetType targetType);
}
