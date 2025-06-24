package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.notification.command.application.service.NotificationService;
import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.application.service.ParticipantService;
import com.ideality.coreflow.project.command.domain.aggregate.Participant;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.command.domain.repository.ParticipantRepository;
import com.ideality.coreflow.project.query.mapper.ProjectMapper;
import com.ideality.coreflow.project.query.mapper.WorkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ideality.coreflow.notification.command.domain.aggregate.TargetType.PROJECT;
import static com.ideality.coreflow.notification.command.domain.aggregate.TargetType.WORK;


@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository participantRepository;

    @Override
    public void createParticipants(ParticipantDTO taskParticipant) {
        Participant participant = Participant.builder()
                .targetType(taskParticipant.getTargetType())
                .targetId(taskParticipant.getTargetId())
                .userId(taskParticipant.getUserId())
                .roleId(taskParticipant.getRoleId())
                .build();

        participantRepository.save(participant);
    }


    @Transactional
    @Override
    public void createAssignee(ParticipantDTO assigneeDTO) {
        Participant participant = Participant.builder()
                .targetType(assigneeDTO.getTargetType())
                .targetId(assigneeDTO.getTargetId())
                .userId(assigneeDTO.getUserId())
                .roleId(assigneeDTO.getRoleId())
                .build();

        participantRepository.save(participant);
    }




    @Transactional
    @Override
    public void updateAssignee(Long taskId, ParticipantDTO assigneeDTO) {
        // 기존 담당자 조회
        Optional<Participant> existingAssigneeOptional = participantRepository.findByTargetIdAndRoleId(taskId, 6L); // 6L은 담당자 역할 ID

        if (existingAssigneeOptional.isPresent()) {
            Participant existingAssignee = existingAssigneeOptional.get();

            // 수정할 필드 적용
            existingAssignee.setUserId(assigneeDTO.getUserId());
            existingAssignee.setRoleId(assigneeDTO.getRoleId());
            existingAssignee.setTargetType(assigneeDTO.getTargetType());
            existingAssignee.setTargetId(assigneeDTO.getTargetId());

            // 저장
            participantRepository.save(existingAssignee);
            log.info("책임자 수정 완료: {}", assigneeDTO.getUserId());
        } else {
            log.warn("책임자 정보가 존재하지 않음, 새로 추가합니다.");
            // 기존 담당자가 없으면 새로 추가
            createAssignee(assigneeDTO);  // 기존 로직 사용
        }
    }


    @Transactional
    @Override
    public void updateParticipants(Long taskId, List<Long> participantIds) {
        // 기존 참여자 (roleId가 7인 경우) 삭제
        participantRepository.deleteByTargetIdAndRoleId(taskId, 7L); // taskId와 roleId 7인 참여자만 삭제

        // 새로운 참여자 추가
        for (Long participantId : participantIds) {
            ParticipantDTO participantDTO = ParticipantDTO.builder()
                    .targetType(TargetType.DETAILED)
                    .targetId(taskId)
                    .userId(participantId)
                    .roleId(7L)  // 참여자 역할 ID
                    .build();

            createAssignee(participantDTO);  // 참여자를 새로 추가하는 로직 사용
            log.info("참여자 추가 완료: {}", participantId);
        }
    }

    @Override
    public boolean isParticipant(long targetId, long userId, TargetType targetType) {
        return participantRepository.existsByTargetIdAndUserIdAndTargetType(targetId, userId, targetType);
    }
}
