package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.application.dto.RequestDeleteParticipant;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;

import java.util.List;

public interface ParticipantService {
    void createParticipants(ParticipantDTO taskParticipants);
    void createAssignee(ParticipantDTO assigneeDTO);
    void updateAssignee(Long taskId, ParticipantDTO assigneeDTO);
    void updateParticipants(Long taskId, List<Long> participantIds);
    boolean isParticipant(long targetId, long userId, TargetType targetType);

    void addMemberToProject(Long participantId, Long projectId);

    void addMemberToTask(Long participantId, Long parentTaskId);

    void deleteParticipant(Long userId, Long targetId, TargetType targetType);
}
