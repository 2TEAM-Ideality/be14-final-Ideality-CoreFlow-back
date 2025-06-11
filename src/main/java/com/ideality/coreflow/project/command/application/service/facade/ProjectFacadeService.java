package com.ideality.coreflow.project.command.application.service.facade;

import static java.awt.SystemColor.info;

import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.application.dto.RequestDetailDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.application.service.*;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.query.dto.TaskDeptDTO;
import com.ideality.coreflow.project.query.dto.TaskProgressDTO;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import com.ideality.coreflow.project.query.service.ProjectQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.project.query.service.WorkQueryService;
import com.ideality.coreflow.template.query.dto.EdgeDTO;
import com.ideality.coreflow.template.query.dto.NodeDTO;
import com.ideality.coreflow.template.query.dto.TemplateDataDTO;
import com.ideality.coreflow.template.query.dto.NodeDataDTO;
import com.ideality.coreflow.user.query.service.UserQueryService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
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
    private final DetailService detailService;
    private final TaskQueryService taskQueryService;
    private final WorkService workService;
    private final WorkQueryService workQueryService;

    public Double updateProgressRate(Long taskId) {
        List<TaskProgressDTO> workList = workQueryService.getDetailProgressByTaskId(taskId);
        System.out.println("workList.size() = " + workList.size());
        return taskService.updateTaskProgress(taskId, workList);
    }

    @Transactional
    public Double updateProjectProgressRate(Long projectId){
        List<TaskProgressDTO> taskList = taskQueryService.getTaskProgressByProjectId(projectId);
        System.out.println("taskList.size() = " + taskList.size());
        return projectService.updateProjectProgress(projectId, taskList);
    }

    @Transactional
    public Double updatePassedRate(Long workId){
        return workService.updatePassedRate(workId);
    }

    @Transactional
    public Double updateProjectPassedRate(Long projectId){
        return projectService.updateProjectPassedRate(projectId);
    }

    @Transactional
    public Long updateProjectStatus(Long projectId, Long userId, Status targetStatus)
            throws NotFoundException, IllegalAccessException {
        Project project = projectService.findById(projectId);

        if(project.getStatus()==targetStatus){
            throw new IllegalStateException("이미 '" + targetStatus + "' 상태입니다.");
        }

        if(!participantQueryService.isProjectDirector(projectId, userId)){
            throw new IllegalAccessException("이 프로젝트의 디렉터가 아닙니다");
        }

        if (targetStatus == Status.COMPLETED) {
            if (project.getStatus() != Status.PROGRESS) {
                throw new IllegalStateException("진행중(PROGRESS) 상태의 프로젝트만 완료(COMPLETED)로 전환할 수 있습니다");
            }
            if (!taskQueryService.isAllTaskCompleted(projectId)) {
                throw new IllegalStateException("모든 태스크가 완료되지 않았습니다");
            }
        }

        if (targetStatus == Status.PENDING &&
                !EnumSet.of(Status.DELETED, Status.CANCELLED).contains(project.getStatus())) {
            throw new IllegalStateException(project.getStatus()+" 상태에서는 PENDING으로 전환할 수 없습니다");
        }

        return projectService.updateProjectStatus(project, targetStatus);
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
        NodeDataDTO data = node.getData();

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
            // target이 없으면
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


    @Transactional
    public Long createDetail(RequestDetailDTO requestDetailDTO) {
        Long detailId = detailService.createDetail(requestDetailDTO);
        log.info("세부 일정 생성");

        //1. source와 target 모두 null일 경우, 관계 설정을 생략
        if ((requestDetailDTO.getSource() == null || requestDetailDTO.getSource().isEmpty()) &&
                (requestDetailDTO.getTarget() == null || requestDetailDTO.getTarget().isEmpty())) {
            log.info("source와 target 모두 null이므로 관계 설정을 생략합니다.");
        } else {

            if (requestDetailDTO.getSource() == null || requestDetailDTO.getSource().isEmpty()) {
                //2. source가 없고, target만 있을 때 관계 설정
                if (requestDetailDTO.getTarget() != null && !requestDetailDTO.getTarget().isEmpty()) {
                    relationService.appendTargetRelation(requestDetailDTO.getTarget(), detailId); // target에 대한 관계 설정
                }
            } else {
                if (requestDetailDTO.getTarget() == null || requestDetailDTO.getTarget().isEmpty()) {
                    // 3.source는 있고 target이 없을 때
                    relationService.appendRelation(requestDetailDTO.getSource(), detailId);
                } else {
                    //4.source와 target 둘 다 있을 때

                    relationService.appendMiddleRelation(requestDetailDTO.getSource(), requestDetailDTO.getTarget(), detailId);
                }
            }
            log.info("세부 일정 관계 설정");
        }

        Long deptId = requestDetailDTO.getDeptId();
        workDeptService.createWorkDept(detailId, deptId);
        log.info("참여 부서 설정 완료");

        //DTO로 담당자 정보 받아오기
        ParticipantDTO AssigneeDTO = ParticipantDTO.builder()
                .targetType(TargetType.DETAILED)
                .taskId(detailId)
                .userId(requestDetailDTO.getAssigneeId())
                .roleId(6L)
                .build();
        participantService.createAssignee(AssigneeDTO);
        log.info("책임자 설정 완료");

        // 여러 명의 참여자 ID 처리
        for (Long participantId : requestDetailDTO.getParticipantIds()) {
            // 담당자 DTO 생성
            ParticipantDTO participants = ParticipantDTO.builder()
                    .targetType(TargetType.DETAILED)
                    .taskId(detailId)  // 해당 세부일정의 ID
                    .userId(participantId)  // 참여자 ID
                    .roleId(7L)  // 참여자임을 의미
                    .build();

            // 서비스 메서드에 DTO 전달
            participantService.createAssignee(participants);
            log.info("참여자 설정 완료: {}", participantId);
        }

        return detailId;
    }

    @Transactional
    public Long updateDetail(Long detailId, RequestDetailDTO requestDetailDTO) {
        // DetailService에서 세부 일정 수정 로직 호출
        return detailService.updateDetail(detailId, requestDetailDTO);
    }

    // 1. 시작 버튼 (Status: PROGRESS, startReal: 현재 날짜)
    public void startDetail(Long workId) {
        detailService.startDetail(workId);  // 실제 비즈니스 로직은 WorkService에서 처리
    }

    // 2. 완료 버튼 (Status: COMPLETED, endReal: 현재 날짜, progressRate가 100일 경우)
    public void completeDetail(Long workId) {
        detailService.completeDetail(workId);  // 실제 비즈니스 로직은 WorkService에서 처리
    }

    // 3. 삭제 버튼 (Status: DELETED)
    public void deleteDetail(Long workId) {
        detailService.deleteDetail(workId);  // 실제 비즈니스 로직은 WorkService에서 처리
    }

}
