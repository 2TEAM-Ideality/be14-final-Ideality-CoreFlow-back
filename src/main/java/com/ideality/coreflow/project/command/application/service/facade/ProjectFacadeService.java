package com.ideality.coreflow.project.command.application.service.facade;

import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.application.service.*;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import com.ideality.coreflow.template.query.dto.EdgeDTO;
import com.ideality.coreflow.template.query.dto.NodeDTO;
import com.ideality.coreflow.template.query.dto.TemplateNodeDataDTO;
import com.ideality.coreflow.user.query.service.UserQueryService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.modeler.ParameterInfo;
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
                        .userId(leaderId)
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
        if(request.getTemplateData()!=null) {
            // 엣지 정보 다 뽑아내기
            List<String[]> edgeList=new ArrayList<>();
            for(EdgeDTO edge:request.getTemplateData().getEdgeList()){
                edgeList.add(new String[]{edge.getSource(), edge.getTarget()});
            }
            // 태스크 생성 및 edgeList 업데이트
            for(NodeDTO node : request.getTemplateData().getNodeList()){
                String nodeId=node.getId();
                TemplateNodeDataDTO data=node.getData();
                RequestTaskDTO requestTaskDTO=RequestTaskDTO.builder()
                        .label(data.getLabel())
                        .description(data.getDescription())
                        .startBaseLine(LocalDate.parse(data.getStartBaseLine()))
                        .endBaseLine(LocalDate.parse(data.getEndBaseLine()))
                        .projectId(project.getId())
                        .build();
                // Task 생성
                Long taskId=taskService.createTask(requestTaskDTO);
                // work_dept 정보 삽입
                List<Long> deptList=data.getDeptList();
                for(Long deptId:deptList) {
                    workDeptService.createWorkDept(taskId, deptId);
                }
                // 각 태스크에 팀장 추가 (participant)
                List<ParticipantDTO> taskLeaders=new ArrayList<>();
                for(ParticipantDTO leader:leaders) {
                    // 1. 팀장의 부서 이름 조회
                    String leaderDeptName=userQueryService.findDeptNameByUserId(leader.getUserId());
                    // 2. 부서 이름으로 부서id 조회
                    Long leaderDeptId=deptQueryService.findIdByName(leaderDeptName);
                    // 3. 현재 태스크의 참여 부서와 비교
                    if(deptList.contains(leaderDeptId)) {
                        // 4. 해당 리더를 태스크의 팀장으로 등록
                        ParticipantDTO taskLeader=ParticipantDTO.builder()
                                .taskId(taskId)
                                .userId(leader.getUserId())
                                .targetType(TargetType.TASK)
                                .roleId(2L)
                                .build();
                        taskLeaders.add(taskLeader);
                    }
                }
                participantService.createParticipants(taskLeaders);
                // edgeList 업데이트
                System.out.println("taskId = " + taskId + "생성 완료.");
                for (String[] edge : edgeList) {
                    for (int i = 0; i < edge.length; i++) {
                        if (edge[i].equals(nodeId)) {
                            edge[i] = taskId.toString();
                        }
                    }
                }
            }
            // edgeList 저장
            for(String[] edge : edgeList) {
                relationService.createRelation(Long.parseLong(edge[0]), Long.parseLong(edge[1]));
            }
        }
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
