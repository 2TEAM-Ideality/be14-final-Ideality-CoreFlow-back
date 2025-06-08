package com.ideality.coreflow.project.command.application.service.facade;

import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.application.service.*;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.command.domain.repository.ProjectRepository;
import com.ideality.coreflow.project.query.dto.TaskDeptDTO;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.template.query.dto.EdgeDTO;
import com.ideality.coreflow.template.query.dto.NodeDTO;
import com.ideality.coreflow.template.query.dto.TemplateDataDTO;
import com.ideality.coreflow.template.query.dto.TemplateNodeDataDTO;
import com.ideality.coreflow.user.query.service.UserQueryService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
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
    private final ProjectRepository projectRepository;
    private final TaskQueryService taskQueryService;

    @Transactional
    public void completeProject(Long projectId) throws NotFoundException {
        // 1. 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                                            .orElseThrow(()->new NotFoundException("프로젝트가 존재하지 않습니다"));

        // 2. 모든 태스크 완료 여부 확인
        boolean isAllTaskCompleted = taskQueryService.isAllTaskCompleted(projectId);
        if(!isAllTaskCompleted) {
            throw new IllegalStateException("모든 태스크가 완료되지 않았습니다");
        }

        // 3. 상태 변경
        projectService.completeProject(project);

    }

    @Transactional
    public Project createProject(ProjectCreateRequest request) {
        Project project = registerProject(request);
        registerProjectDirector(project.getId(), request.getDirectorId());
        List<ParticipantDTO> leaders = registerProjectLeaders(project.getId(), request.getLeaderIds());
        if (request.getTemplateData() != null){
            applyTemplate(project.getId(), request.getTemplateData(), leaders);
        }
        return project;
    }

    private void applyTemplate(Long projectId, TemplateDataDTO templateData, List<ParticipantDTO> projectLeaders) {
        Map<String, Long> nodeIdToTaskId = new HashMap<>();

        for(NodeDTO node:templateData.getNodeList()){
            Long taskId = createTaskWithDepts(projectId, node);
            nodeIdToTaskId.put(node.getId(), taskId);
            assignTaskLeaders(taskId, node.getData().getDeptList(), projectLeaders);
        }

        for (EdgeDTO edge : templateData.getEdgeList()) {
            Long sourceId = nodeIdToTaskId.get(edge.getSource());
            Long targetId = nodeIdToTaskId.get(edge.getTarget());
            relationService.createRelation(sourceId, targetId);
        }
    }

    private void assignTaskLeaders(Long taskId, List<TaskDeptDTO> taskDeptList, List<ParticipantDTO> projectLeaders) {
        List<Long> taskDeptIds = taskDeptList.stream()
                .map(TaskDeptDTO::getId)
                .toList();

        List<ParticipantDTO> matchedLeaders = new ArrayList<>();

        for(ParticipantDTO leader:projectLeaders){
            String deptName = userQueryService.getDeptNameByUserId(leader.getUserId());
            Long deptId = deptQueryService.findDeptIdByName(deptName);
            if (taskDeptIds.contains(deptId)) {
                matchedLeaders.add(
                        ParticipantDTO.builder()
                                .taskId(taskId)
                                .userId(leader.getUserId())
                                .targetType(TargetType.TASK)
                                .roleId(2L)
                                .build()
                );
            }
        }
        if (!matchedLeaders.isEmpty()) {
            participantService.createParticipants(matchedLeaders);
        }
    }

    private Long createTaskWithDepts(Long projectId, NodeDTO node) {
        TemplateNodeDataDTO data = node.getData();

        RequestTaskDTO taskDTO = RequestTaskDTO.builder()
                .label(data.getLabel())
                .description(data.getDescription())
                .startBaseLine(LocalDate.parse(data.getStartBaseLine()))
                .endBaseLine(LocalDate.parse(data.getEndBaseLine()))
                .projectId(projectId)
                .build();

        Long taskId = taskService.createTask(taskDTO);

        List<Long> deptIds = data.getDeptList().stream()
                .map(TaskDeptDTO::getId)
                .toList();

        for(Long deptId:deptIds){
            workDeptService.createWorkDept(taskId, deptId);
        }
        return taskId;
    }

    private List<ParticipantDTO> registerProjectLeaders(Long projectId, List<Long> leaderIds) {
        if(leaderIds == null || leaderIds.isEmpty()) return List.of();

        List<ParticipantDTO> leaders = leaderIds.stream()
                .map(userId -> ParticipantDTO.builder()
                        .taskId(projectId)
                        .userId(userId)
                        .targetType(TargetType.PROJECT)
                        .roleId(2L)
                        .build()
                ).toList();
        participantService.createParticipants(leaders);
        return leaders;
    }

    private void registerProjectDirector(Long projectId, Long directorId) {
        ParticipantDTO participant = ParticipantDTO.builder()
                .taskId(projectId)
                .userId(directorId)
                .targetType(TargetType.PROJECT)
                .roleId(1L)
                .build();
        participantService.createParticipants(List.of(participant));
    }

    private Project registerProject(ProjectCreateRequest request) {
        return projectService.createProject(request);
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
                .collect(Collectors.toMap(name -> name, userQueryService::selectMentionUserByDeptName));

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
