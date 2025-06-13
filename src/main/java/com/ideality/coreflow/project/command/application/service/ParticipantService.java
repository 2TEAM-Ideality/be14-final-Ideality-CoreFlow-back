package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;

import java.util.List;

public interface ParticipantService {
    void createParticipants(List<ParticipantDTO> taskParticipants);
    void createAssignee(ParticipantDTO assigneeDTO);
    void updateAssignee(Long taskId, ParticipantDTO assigneeDTO);
    void updateParticipants(Long taskId, List<Long> participantIds);
}
