package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.project.command.application.dto.TaskParticipantDTO;
import com.ideality.coreflow.project.command.application.service.ParticipantService;
import com.ideality.coreflow.project.command.domain.aggregate.Participant;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.command.domain.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ideality.coreflow.common.exception.ErrorCode.PARTICIPANT_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository participantRepository;

    @Override
    @Transactional
    public void createParticipants(List<TaskParticipantDTO> taskParticipants) {
        for (TaskParticipantDTO taskParticipant : taskParticipants) {
            Participant participant = Participant.builder()
                    .targetType(TargetType.TASK)
                    .targetId(taskParticipant.getTaskId())
                    .userId(taskParticipant.getUserId())
                    .roleId(3L)
                    .build();
            participantRepository.save(participant);
        }
    }

    @Override
    @Transactional
    public void updateParticipantsLeader(Long teamLeaderId, Long projectId) {
        Participant updateLeader = (Participant) participantRepository
                .findByTargetIdAndUserId(projectId, teamLeaderId)
                .orElseThrow(() -> new BaseException(PARTICIPANT_NOT_FOUND));

        updateLeader.changeRoleId();
    }
}
