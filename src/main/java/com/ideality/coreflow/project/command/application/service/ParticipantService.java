package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.TaskParticipantDTO;

import java.util.List;

public interface ParticipantService {
    void createParticipants(List<TaskParticipantDTO> taskParticipants);
}
