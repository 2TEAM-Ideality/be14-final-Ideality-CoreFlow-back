package com.ideality.coreflow.project.command.application.service.facade;

import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.application.service.*;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import com.ideality.coreflow.user.query.service.UserQueryService;
import java.util.ArrayList;
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
    private final ParticipantQueryService participantQueryService;

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
        // region

//        if(request.getTemplateData()!=null) {
//            Map<String, Long> taskMap=new HashMap<>();
//            for(NodeDTO node : request.getTemplateData().getNodeList()){
//                String nodeId=node.getId();
//                List<Long> sourceIds=new ArrayList<>();
//                List<Long> targetIds=new ArrayList<>();
//                for(EdgeDTO edge:request.getTemplateData().getEdgeList()){
//                    if(nodeId.equals(edge.getSource())){
//                        targetIds.add(Long.parseLong(edge.getTarget()));
//                    } else if (nodeId.equals(edge.getTarget())) {
//                        sourceIds.add(Long.parseLong(edge.getSource()));
//                    }
//                }
//                if (sourceIds.isEmpty()) {
//                    sourceIds.add(0L);
//                }
//                TemplateNodeDataDTO data=node.getData();
//                RequestTaskDTO requestTaskDTO=RequestTaskDTO.builder()
//                        .label(data.getLabel())
//                        .description(data.getDescription())
//                        .startBaseLine(LocalDate.parse(data.getStartBaseLine()))
//                        .endBaseLine(LocalDate.parse(data.getEndBaseLine()))
//                        .projectId(project.getId())
//                        .deptList(data.getDeptList().stream()
//                                .map(Long::valueOf)
//                                .toList())
//                        .source(sourceIds)
//                        .target(targetIds)
//                        .build();
//
//            }
//        }
        // endregion
        return project;
    }

    @Transactional
    public Long createTask(RequestTaskDTO requestTaskDTO) {

        /* 설명. “읽기-쓰기 분리 전략”
         *  중복 select를 방지하기 위해 읽기부터
        * */
        Map<Long, String> deptIdMap = requestTaskDTO.getDeptList().stream()
                        .collect
                        (Collectors.toMap(id -> id, deptQueryService::findNameById));
        log.info("부서 조회 끝");
        List<String> deptNames = deptIdMap.values().stream().distinct().toList();

        Map<String, List<Long>> deptLeaderMaps = deptNames.stream()
                .collect(Collectors.toMap(name -> name, userQueryService::selectLeadersByDeptName));

        Long directorId = participantQueryService.selectDirectorByProjectId(requestTaskDTO.getProjectId());

        Map<String, List<Long>> deptUsersMaps = deptNames.stream()
                .collect(Collectors.toMap(name -> name, userQueryService::selectAllUserByDeptName));

        log.info("조회부터 완료");

        /* 설명. 태스크 부터 */
        Long taskId = taskService.createTask(requestTaskDTO);
        taskService.validateSource(requestTaskDTO.getSource());
        if (requestTaskDTO.getTarget() == null || requestTaskDTO.getTarget().isEmpty()) {
            // target이 없으면 target=null로 관계 생성
            relationService.appendRelation(requestTaskDTO.getSource(), taskId);
        } else {
            taskService.validateTarget(requestTaskDTO.getTarget());
            relationService.appendMiddleRelation(requestTaskDTO.getSource(), requestTaskDTO.getTarget(), taskId);
        }
        log.info("태스크 및 태스트별 관계 설정 완료");


        // ✅ 5. 쓰기 작업 (deptId 기준)
        for (Long deptId : requestTaskDTO.getDeptList()) {
            String deptName = deptIdMap.get(deptId);

            workDeptService.createWorkDept(taskId, deptId);
            log.info("작업 별 참여 부서 생성 완료");

            List<Long> userIds = deptUsersMaps.get(deptName);
            List<Long> leaderIds = deptLeaderMaps.get(deptName);
            // ✅ 1. 팀장 먼저 등록
            List<ParticipantDTO> leaderParticipants = leaderIds.stream()
                    .map(leaderId -> new ParticipantDTO(taskId, leaderId, TargetType.TASK, 2L))
                    .toList();
            participantService.createParticipants(leaderParticipants);
            log.info("팀장 등록 완료");

            // ✅ 2. 팀원 등록 (디렉터 & 팀장 제외)
            List<ParticipantDTO> teamParticipants = userIds.stream()
                    .filter(userId -> !leaderIds.contains(userId))       // 팀장 제외
                    .filter(userId -> !userId.equals(directorId))        // 디렉터 제외
                    .map(userId -> new ParticipantDTO(taskId, userId, TargetType.TASK, 3L))
                    .toList();
            participantService.createParticipants(teamParticipants);
            log.info("팀원 등록 완료");

        }
        return taskId;
    }

    @Transactional
    public Long updateStatusProgress(Long taskId) {
        Long updateTaskId = taskService.updateStatusProgress(taskId);
        return updateTaskId;
    }

    @Transactional
    public Long updateStatusComplete(Long taskId) {
        Long updateTaskId = taskService.updateStatusComplete(taskId);
        return updateTaskId;
    }

    public Long deleteTaskBySoft(Long taskId) {
        Long deleteTaskId = taskService.softDeleteTask(taskId);
        return deleteTaskId;
    }
}
