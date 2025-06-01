package com.ideality.coreflow.project.command.service;

import com.ideality.coreflow.project.command.domain.aggregate.Participant;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.command.domain.repository.ParticipantRepository;
import com.ideality.coreflow.project.command.domain.repository.ProjectRepository;
import com.ideality.coreflow.project.command.dto.ProjectCreateRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ParticipantRepository participantRepository;

    /*  중요:
    *   아래 숫자는 임의로 대입한 숫자임
    *   추후 다음 두가지 방법 중 한가지를 택해서 수정해야 함
    *   1. 테이블에서 검색해서 ID를 찾아오는 방법
    *   2. ROLE_ID를 정해놓고 하드코딩 하는 방법*/
    private static final Long DIRECTOR_ROLE_ID=1L;
    private static final Long TEAM_LEADER_ROLE_ID =2L;

    public Project createProject(ProjectCreateRequest request) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .startBase(request.getStartBase())
                .endBase(request.getEndBase())
                .startExpect(request.getStartBase())
                .endExpect(request.getEndBase())
                .progressRate(0.0)
                .passedRate(0.0)
                .delayDays(0)
                .status(Status.PENDING)
                .build();
        projectRepository.save(project);

        Participant director = Participant.builder()
                .targetType(TargetType.PROJECT)
                .targetId(project.getId())
                .userId(request.getDirectorId())
                .roleId(DIRECTOR_ROLE_ID)
                .build();
        participantRepository.save(director);

        if(request.getLeaderIds()!=null) {
            for(Long leaderId : request.getLeaderIds()) {
                Participant participant=Participant.builder()
                        .targetType(TargetType.PROJECT)
                        .targetId(project.getId())
                        .userId(leaderId)
                        .roleId(TEAM_LEADER_ROLE_ID)
                        .build();
                participantRepository.save(participant);
            }
        }

        return project;
    }
}
