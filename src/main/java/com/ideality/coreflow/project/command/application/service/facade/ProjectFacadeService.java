package com.ideality.coreflow.project.command.application.service.facade;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.notification.command.application.service.NotificationRecipientsService;
import com.ideality.coreflow.notification.command.application.service.NotificationService;
import com.ideality.coreflow.project.command.application.dto.*;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.application.dto.RequestDetailDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.application.service.*;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.query.dto.TaskDeptDTO;
import com.ideality.coreflow.org.query.service.DeptQueryService;
import com.ideality.coreflow.project.query.dto.TaskProgressDTO;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import com.ideality.coreflow.project.query.service.ProjectQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.project.query.service.WorkQueryService;
import com.ideality.coreflow.template.query.dto.EdgeDTO;
import com.ideality.coreflow.template.query.dto.NodeDTO;
import com.ideality.coreflow.template.query.dto.TemplateDataDTO;
import com.ideality.coreflow.template.query.dto.NodeDataDTO;
import com.ideality.coreflow.user.command.application.service.UserService;
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
    private final UserService userService;
    private final NotificationService notificationService;
    private final NotificationRecipientsService notificationRecipientsService;

    private final DeptQueryService deptQueryService;
    private final UserQueryService userQueryService;
    private final ParticipantQueryService participantQueryService;
    private final DetailService detailService;
    private final TaskQueryService taskQueryService;
    private final WorkService workService;
    private final WorkQueryService workQueryService;
    private final ProjectQueryService projectQueryService;

    public Double updateProgressRate(Long taskId) {
        return taskService.updateTaskProgress(taskId);
    }

    @Transactional
    public Double updateProjectProgressRate(Long projectId){
        return projectService.updateProjectProgress(projectId);
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
    public Long createTask(RequestTaskDTO requestTaskDTO, Long userId) {

        /* 설명. “읽기-쓰기 분리 전략”
         *  중복 select를 방지하기 위해 읽기부터
        * */

        boolean isParticipant = participantQueryService.isParticipant(userId, requestTaskDTO.getProjectId());

        if (!isParticipant) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

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

        if (requestTaskDTO.getSource() != null && requestTaskDTO.getTarget() != null) {
            // 검증 부터 수행
            taskService.validateRelation(requestTaskDTO.getSource());
            taskService.validateRelation(requestTaskDTO.getTarget());

            // 실제 값을 넣기 -> 이 부분을 수정했음
            relationService.appendRelation(taskId, requestTaskDTO.getSource(), requestTaskDTO.getTarget());

        } else {
            log.info("둘 다 null이라 값을 넣지 않음");
        }
//        if (requestTaskDTO.getTarget() == null || requestTaskDTO.getTarget().isEmpty()) {
//            // target이 없으면
//        } else {
//            relationService.appendMiddleRelation(requestTaskDTO.getSource(), requestTaskDTO.getTarget(), taskId);
//        }
        log.info("태스크 및 태스트별 관계 설정 완료");


        // ✅ 5. 쓰기 작업 (deptId 기준)
        for (Long deptId : requestTaskDTO.getDeptList()) {
            String deptName = deptIdMap.get(deptId);

            workDeptService.createWorkDept(taskId, deptId);
            log.info("작업 별 참여 부서 생성 완료");

            List<Long> newParticipantsIds = deptUsersMaps.get(deptName);
            List<Long> leaderIds = deptLeaderMaps.get(deptName);
            // ✅ 1. 팀장 먼저 등록
            List<ParticipantDTO> leaderParticipants = leaderIds.stream()
                    .map(leaderId -> new ParticipantDTO(taskId, leaderId, TargetType.TASK, 2L))
                    .toList();
            participantService.createParticipants(leaderParticipants);
            log.info("팀장 등록 완료");

            // ✅ 2. 팀원 등록 (디렉터 & 팀장 제외)
            List<ParticipantDTO> teamParticipants = newParticipantsIds.stream()
                    .filter(participantUserId -> !leaderIds.contains(userId))       // 팀장 제외
                    .filter(participantUserId -> !userId.equals(directorId))        // 디렉터 제외
                    .map(participantUserId -> new ParticipantDTO(taskId, userId, TargetType.TASK, 3L))
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

    @Transactional
    public Long deleteTaskBySoft(Long taskId) {
        Long deleteTaskId = taskService.softDeleteTask(taskId);
        return deleteTaskId;
    }


    @Transactional
    public Long createDetail(RequestDetailDTO requestDetailDTO, Long userId) {

        boolean isParticipant = participantQueryService.isParticipant(userId, requestDetailDTO.getProjectId());
        if (!isParticipant) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
        Long detailId = detailService.createDetail(requestDetailDTO);
        log.info("세부 일정 생성");

        //1. source와 target 모두 null일 경우, 관계 설정을 생략
        if ((requestDetailDTO.getSource() == null || requestDetailDTO.getSource().isEmpty()) &&
                (requestDetailDTO.getTarget() == null || requestDetailDTO.getTarget().isEmpty())) {
            log.info("source와 target 모두 null이므로 관계 설정을 생략합니다.");
        } else {

//            if (requestDetailDTO.getSource() == null || requestDetailDTO.getSource().isEmpty()) {
//                //2. source가 없고, target만 있을 때 관계 설정
//                if (requestDetailDTO.getTarget() != null && !requestDetailDTO.getTarget().isEmpty()) {
//                    relationService.appendTargetRelation(requestDetailDTO.getTarget(), detailId); // target에 대한 관계 설정
//                }
//            } else {
//                if (requestDetailDTO.getTarget() == null || requestDetailDTO.getTarget().isEmpty()) {
//                    // 3.source는 있고 target이 없을 때
//                    relationService.appendRelation(requestDetailDTO.getSource(), detailId);
//                } else {
//                    //4.source와 target 둘 다 있을 때
//
//                    relationService.appendMiddleRelation(requestDetailDTO.getSource(), requestDetailDTO.getTarget(), detailId);
//                }
//            }
            relationService.appendRelation(detailId, requestDetailDTO.getSource(), requestDetailDTO.getTarget());
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

    @Transactional
    public void createParticipantsLeader(Long userId, Long projectId, List<RequestInviteUserDTO> reqLeaderDTO) {

        projectService.existsById(projectId);
        boolean isDirector = participantQueryService.isProjectDirector(projectId, userId);
        if (!isDirector) {
            throw new BaseException(ErrorCode.TEAM_LEADER_ALREADY_EXISTS);
        }

        List<Long> leaderUserIds = reqLeaderDTO.stream()
                .map(RequestInviteUserDTO::getUserId)
                .collect(Collectors.toList());
        // 2가지 예외 처리 회원 id 값들이 제대로 된 회원 값이냐
        // request 된 값으로 된 팀장이 이미 존재하거나 -> 팀장은 1

        userService.existsUserId(leaderUserIds);
        participantQueryService.findTeamLedaer(projectId, reqLeaderDTO);

        // 리더 삽입 -> 기존 로직 활용
        List<ParticipantDTO> leaders = leaderUserIds.stream()
                .map(leaderId -> ParticipantDTO.builder()
                        .taskId(projectId)
                        .userId(leaderId)
                        .targetType(TargetType.PROJECT)
                        .roleId(2L)
                        .build()
                ).toList();
        participantService.createParticipants(leaders);

        // 초대 됐다는 알림 작성
        String writerName = userQueryService.getUserId(userId);
        String projectName = projectQueryService.getProjectName(projectId);
        String content = String.format("%s 님이 회원님을 %s에 초대하였습니다.", writerName, projectName);
        Long notificationId = notificationService.createInviteProject(projectId, content);
        notificationRecipientsService.createRecipients(leaderUserIds, notificationId);

    }

    @Transactional
    public void createParticipantsTeamLeader(Long userId, Long projectId, List<RequestInviteUserDTO> reqMemberDTO) {
        projectService.existsById(projectId);
        // 권한 확인 필요 -> 팀장이거나 or 디렉터
        boolean isInviteRole = participantQueryService.isAboveTeamLeader(userId, projectId);
        if (!isInviteRole) {
            throw new BaseException(ErrorCode.TEAM_MEMBER_ALREADY_EXISTS);
        }

        List<Long> participantUser = reqMemberDTO.stream()
                .map(RequestInviteUserDTO::getUserId)
                .collect(Collectors.toList());

        // 사용자 id가 적절한지 확인
        userService.existsUserId(participantUser);
        // 혹시 이미 팀원인 사람 초대 했는지 확인
        participantQueryService.alreadyExistsMember(projectId, reqMemberDTO);

        // 이제 참여자 초대
        List<ParticipantDTO> teamMember = participantUser.stream()
                .map(leaderId -> ParticipantDTO.builder()
                        .taskId(projectId)
                        .userId(leaderId)
                        .targetType(TargetType.PROJECT)
                        .roleId(3L)
                        .build()
                ).toList();
        participantService.createParticipants(teamMember);

        // 초대 됐다는 알림 작성
        String writerName = userQueryService.getUserId(userId);
        String projectName = projectQueryService.getProjectName(projectId);
        String content = String.format("%s 님이 회원님을 %s에 초대하였습니다.", writerName, projectName);
        Long notificationId = notificationService.createInviteProject(projectId, content);
        notificationRecipientsService.createRecipients(participantUser, notificationId);
    }
}
