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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ideality.coreflow.notification.command.domain.aggregate.TargetType.PROJECT;
import static com.ideality.coreflow.notification.command.domain.aggregate.TargetType.WORK;


@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository participantRepository;
    private final NotificationService notificationService;
    private final WorkMapper workMapper;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional
    public void createParticipants(List<ParticipantDTO> taskParticipants) {
        for (ParticipantDTO taskParticipant : taskParticipants) {
            Participant participant = Participant.builder()
                    .targetType(taskParticipant.getTargetType())
                    .targetId(taskParticipant.getTaskId())
                    .userId(taskParticipant.getUserId())
                    .roleId(taskParticipant.getRoleId())
                    .build();

            participantRepository.save(participant);

            // taskParticipant.getTargetType()에 따라 알림 내용 설정
            if (taskParticipant.getRoleId() == 2L) { // roleId가 2L이면 팀장
                String content = "";

                if (taskParticipant.getTargetType() == TargetType.TASK) { // TARGET TYPE이 TASK일 때
                    // WORK 테이블에서 태스크 이름 조회
                    String taskName = workMapper.findTaskNameByTaskId(taskParticipant.getTaskId());
                    content = "태스크 [" + taskName + "]에 팀장으로 초대되었습니다.";
                    // 알림 전송
                    notificationService.sendNotification(taskParticipant.getUserId(), content, taskParticipant.getTaskId(), WORK);
                } else if (taskParticipant.getTargetType() == TargetType.PROJECT) { // TARGET TYPE이 PROJECT일 때
                    // PROJECT 테이블에서 프로젝트 이름 조회
                    String projectName = projectMapper.findProjectNameByProjectId(taskParticipant.getTaskId());
                    content = "프로젝트 [" + projectName + "]에 팀장으로 초대되었습니다.";
                    notificationService.sendNotification(taskParticipant.getUserId(), content, taskParticipant.getTaskId(), PROJECT);
                }
            }
        }
    }


    @Transactional
    @Override
    public void createAssignee(ParticipantDTO assigneeDTO) {
        Participant participant = Participant.builder()
                .targetType(assigneeDTO.getTargetType())
                .targetId(assigneeDTO.getTaskId())
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
            existingAssignee.setTargetId(assigneeDTO.getTaskId());

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
                    .taskId(taskId)
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
