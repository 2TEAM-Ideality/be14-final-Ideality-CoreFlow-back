package com.ideality.coreflow.project.command.application.service.facade;

import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.application.service.*;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.template.query.dto.NodeDTO;
import com.ideality.coreflow.template.query.dto.TemplateDataDTO;
import com.ideality.coreflow.user.query.service.UserQueryService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectFacadeService {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final RelationService relationService;
    private final WorkDeptService workDeptService;
    private final ParticipantService participantService;

    private final DeptQueryService deptQueryService;
    private final UserQueryService userQueryService;

    public Project createProject(ProjectCreateRequest request) {
        // 프로젝트 생성
        Project project=projectService.createProject(request);
        // 디렉터 DTO 생성
        // region
        List<ParticipantDTO> director=new ArrayList<>();
        ParticipantDTO participant=ParticipantDTO.builder()
                .taskId(project.getId())
                .userId(request.getDirectorId())
                .targetType(TargetType.PROJECT)
                .roleId(1L)
                .build();
        director.add(participant);
        // endregion
        // 디렉터 저장
        participantService.createParticipants(director);
        // 리더 정보 생성
        // region
        List<ParticipantDTO> leaders=new ArrayList<>();
        if(request.getLeaderIds()!=null) {
            for(Long leaderId:request.getLeaderIds()) {
                participant=ParticipantDTO.builder()
                        .taskId(project.getId())
                        .userId(request.getDirectorId())
                        .targetType(TargetType.PROJECT)
                        .roleId(2L)
                        .build();
                leaders.add(participant);
            }
        }
        // endregion
        // 리더 정보 저장
        participantService.createParticipants(leaders);
        // 태스크
        if(request.getTemplateData()!=null) {
            Map<String, RequestTaskDTO> taskMap = new TreeMap<>();
            for(NodeDTO node : request.getTemplateData().getNodeList()){
                RequestTaskDTO requestTaskDTO=RequestTaskDTO.builder()
                        .label(node.getData().getLabel())
                        .description(node.getData().getDescription())
                        .startBaseLine(LocalDate.parse(node.getData().getStartBaseLine()))
                        .endBaseLine(LocalDate.parse(node.getData().getEndBaseLine()))
                        .projectId(project.getId())
//                        .deptList(node.getData().getDeptList())
                        .build();
            }
        }

        return project;
    }

    @Transactional
    public Long createTask(RequestTaskDTO requestTaskDTO) {
        /* 설명.
         *  실제 순서 수정 -> fall fast 원칙으로
         *  유효성 검사를 수행 한 다음에, db에 write를 수행할 것
        *  */

        /* 설명. 부서 id 조회 -> 조회 해오면서, 예외처리까지
         *  이것 또한 fall-fast 원칙에 맞게 수행
        * */
        List<Long> deptIds = requestTaskDTO.getDeptList().stream()
                .map(deptQueryService::findIdByName)
                .toList();
        log.info("부서 조회 끝");

        /* 설명. 태스크 부터 */
        Long taskId = taskService.createTask(requestTaskDTO);
        relationService.appendRelation
                (requestTaskDTO.getSource(), requestTaskDTO.getTarget(), taskId);
        log.info("태스크 및 태스트별 관계 설정 완료");

        /* 설명.
         *   중복된 부서 이름에 대해 반복적으로 조회(select)를 수행하는 것을 방지하기 위해,
         *   부서 이름 목록을 중복 제거한 후(deptNames) 필요한 데이터(부서 ID, 부서별 유저 목록, 팀장 ID)를
         *   한 번씩만 조회하여 Map 형태로 캐싱해둠.
         *   이후 실제 쓰기 작업 (workDept, participant 등록, 팀장 설정)은 원래 순서를 유지한 채
         *   해당 Map에서 값을 꺼내 사용함으로써, 읽기 횟수를 줄이고 효율적인 로직 흐름을 유지함.
         *   → 읽기 작업을 쓰기 로직과 분리하여, I/O 비용을 줄이고 성능을 최적화함
        * */
        List<String> deptNames = requestTaskDTO.getDeptList().stream().distinct().toList();

        Map<String, Long> deptIdMap = deptNames.stream()
                .collect(Collectors.toMap(name -> name, deptQueryService::findIdByName));

        Map<String, List<Long>> deptUsersMap = deptNames.stream()
                .collect(Collectors.toMap(name -> name, userQueryService::selectAllUserByDeptName));

        Map<String, Long> deptLeaderMap = deptNames.stream()
                .collect(Collectors.toMap(name -> name, userQueryService::selectLeaderByDeptName));

        for (String deptName : requestTaskDTO.getDeptList()) {
            Long deptId = deptIdMap.get(deptName);
            workDeptService.createWorkDept(taskId, deptId);

            List<Long> userIds = deptUsersMap.get(deptName);
            List<ParticipantDTO> participants = userIds.stream()
                    .map(userId -> new ParticipantDTO(taskId, userId, TargetType.TASK, 3L))
                    .toList();
            participantService.createParticipants(participants);
            log.info("여기까지는 정상");
            Long leaderId = deptLeaderMap.get(deptName);
            log.info("leader id: {}", leaderId.toString());
            participantService.updateTeamLeader(leaderId, taskId, TargetType.TASK);
        }
        return taskId;
    }
}
