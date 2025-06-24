package com.ideality.coreflow.project.command.application.service.facade;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import com.ideality.coreflow.approval.query.dto.ProjectApprovalDTO;
import com.ideality.coreflow.approval.query.service.ApprovalQueryService;
import com.ideality.coreflow.attachment.query.dto.ReportAttachmentDTO;
import com.ideality.coreflow.attachment.query.service.AttachmentQueryService;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.holiday.query.dto.HolidayQueryDto;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.notification.command.application.service.NotificationRecipientsService;
import com.ideality.coreflow.notification.command.application.service.NotificationService;
import com.ideality.coreflow.notification.command.domain.aggregate.NotificationTargetType;
import com.ideality.coreflow.org.query.service.DeptQueryService;
import com.ideality.coreflow.project.command.application.dto.*;
import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.application.service.*;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.service.WorkDomainService;
import com.ideality.coreflow.project.query.dto.*;
import com.ideality.coreflow.project.query.service.*;
import com.ideality.coreflow.template.query.dto.EdgeDTO;
import com.ideality.coreflow.template.query.dto.NodeDTO;
import com.ideality.coreflow.template.query.dto.TemplateDataDTO;
import com.ideality.coreflow.template.query.dto.NodeDataDTO;
import com.ideality.coreflow.user.command.application.service.RoleService;
import com.ideality.coreflow.user.command.application.service.UserService;
import com.ideality.coreflow.user.query.service.UserQueryService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static com.ideality.coreflow.notification.command.domain.aggregate.NotificationTargetType.PROJECT;
import static com.ideality.coreflow.notification.command.domain.aggregate.NotificationTargetType.WORK;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectFacadeService {

    private final ProjectService projectService;
    private final ProjectQueryService projectQueryService;
    private final TaskService taskService;
    private final RelationService relationService;
    private final WorkDeptService workDeptService;
    private final ParticipantService participantService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final NotificationRecipientsService notificationRecipientsService;

    private final DeptQueryService deptQueryService;
    private final PdfService pdfService;

    private final UserQueryService userQueryService;
    private final ParticipantQueryService participantQueryService;
    private final DetailService detailService;
    private final TaskQueryService taskQueryService;
    private final AttachmentQueryService attachmentQueryService;
    private final ApprovalQueryService approvalQueryService;
    private final WorkService workService;
    private final WorkQueryService workQueryService;
    private final HolidayQueryService holidayQueryService;
    private final RelationQueryService relationQueryService;
    private final RoleService roleService;
    private final WorkDomainService workDomainService;

    @PersistenceContext
    private EntityManager em;

    //
    @Transactional
    public Double updateProgressRate(Long taskId) {

        // 태스크에 종속된 세부일정 조회
        List<WorkProgressDTO> detailList = workQueryService.getDetailProgressByTaskId(taskId);

        // 세부일정을 이용하여 태스크 진척률 계산
        double progress = workDomainService.calculateProgressRate(detailList);

        //태스크 진척률 업데이트
        taskService.updateProgressRate(taskId, progress);
        long projectId = workService.findProjectIdByTaskId(taskId);

        // 프로젝트 진척률도 수정
        updateProjectProgressRate(projectId);

        return progress;
    }

    //
    @Transactional
    public Double updateProjectProgressRate(Long projectId) {

        // 프로젝트에 종속된 태스크 조회
        List<WorkProgressDTO> taskList = taskQueryService.getTaskProgressByProjectId(projectId);

        // 태스크를 이용하여 프로젝트 진척률 계산
        double progress = workDomainService.calculateProgressRate(taskList);

        return projectService.updateProjectProgress(projectId, progress);
    }

    //
    @Transactional
    public Double updatePassedRate(Long targetId, TargetType type) {
        DateInfoDTO dateInfo;

        if (type == TargetType.PROJECT) {
            dateInfo = projectService.findDateInfoByProjectId(targetId);
        } else {
            dateInfo = workService.findDateInfoByWorkId(targetId);
        }

        double passedRate = workDomainService.calculatePassedRate(dateInfo, type);
        log.info("passedRate: {}", passedRate);

        if (type == TargetType.PROJECT) {
            return projectService.updateProjectPassedRate(targetId, passedRate);
        } else {
            return workService.updatePassedRate(targetId, passedRate);
        }
    }

    //
    @Transactional
    public Long updateProjectStatus(Long projectId, Long userId, Status targetStatus) {
        if(!participantQueryService.isProjectDirector(projectId, userId)){
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        if (targetStatus == Status.COMPLETED && !taskQueryService.isAllTaskCompleted(projectId)) {
            throw new BaseException(ErrorCode.NOT_COMPLETED_TASK);
        }

        return projectService.updateProjectStatus(projectId, targetStatus);
    }

    //
    @Transactional
    public Project createProject(ProjectCreateRequest request) {
        Project project = projectService.createProject(request);
        long roleDirectorId = roleService.findRoleByName("DIRECTOR");
        long roleTeamLeaderId = roleService.findRoleByName("TEAM_LEADER");
        long projectId = project.getId();

        // 참여자 리스트
        List<ParticipantDTO> participantList = new ArrayList<>();

        // 디렉터
        ParticipantDTO director = createProjectParticipantDTO(projectId, request.getDirectorId(), roleDirectorId);
        participantList.add(director);

        // 팀장 리스트
        if (request.getLeaderIds() != null) {
            for (Long leaderId : request.getLeaderIds()) {
                ParticipantDTO leader = createProjectParticipantDTO(projectId, leaderId, roleTeamLeaderId);
                participantList.add(leader);
            }
        }

        // 참여자 테이블 삽입
        for (ParticipantDTO participant : participantList) {
            participantService.createParticipants(participant);
            // 알림
            participantNotification(participant);
        }

        TemplateDataDTO templateData = request.getTemplateData();
        if (templateData != null){
            applyTemplate(projectId, request.getTemplateData(), participantList);
//            Map<String, Long> nodeIdToTaskId = new HashMap<>();

//            for(NodeDTO node : templateData.getNodeList()){
//                Long taskId = createTaskWithDepts(projectId, node);
//                nodeIdToTaskId.put(node.getId(), taskId);
//                assignTaskLeaders(taskId, node.getData().getDeptList(), projectLeaders);
//            }
//
//            for (EdgeDTO edge : templateData.getEdgeList()) {
//                Long sourceId = nodeIdToTaskId.get(edge.getSource());
//                Long targetId = nodeIdToTaskId.get(edge.getTarget());
//                relationService.createRelation(sourceId, targetId);
//            }
        }
        return project;
    }

    // 참여자 초대 알림
    private void participantNotification(ParticipantDTO participant) {
        long roleTeamLeaderId = roleService.findRoleByName("TEAM_LEADER");
        long roleTeamMemberId = roleService.findRoleByName("TEAM_MEMBER");

        String roleLabel = null;

        if (participant.getRoleId() == roleTeamLeaderId) {
            roleLabel = "팀장";
        } else if (participant.getRoleId() == roleTeamMemberId) {
            roleLabel = "팀원";
        }

        if (roleLabel == null) return;
        String targetName = getTargetName(participant.getTargetType(), participant.getTargetId());
        String content = targetName != null
                ? targetName + "에 " + roleLabel + "으로 초대되었습니다."
                : null;

        if (content != null) {
            NotificationTargetType type = getNotificationType(participant.getTargetType());
            notificationService.sendNotification(participant.getUserId(), content, participant.getTargetId(), type);
        }
    }

    private NotificationTargetType getNotificationType(TargetType targetType) {
        return targetType == TargetType.TASK ? NotificationTargetType.WORK : NotificationTargetType.PROJECT;
    }

    private String getTargetName(TargetType targetType, Long targetId) {
        if (targetType == TargetType.TASK) {
            String taskName = workQueryService.findTaskNameByTaskId(targetId);
            return "태스크 [" + taskName + "]";
        } else if (targetType == TargetType.PROJECT) {
            String projectName = projectQueryService.findProjectNameByProjectId(targetId);
            return "프로젝트 [" + projectName + "]";
        }
        return null;
    }

    private ParticipantDTO createProjectParticipantDTO(long projectId, Long userId, long roleId) {
        return ParticipantDTO.builder()
                .targetId(projectId)
                .userId(userId)
                .targetType(TargetType.PROJECT)
                .roleId(roleId)
                .build();
    }

    private void applyTemplate(Long projectId, TemplateDataDTO templateData, List<ParticipantDTO> projectLeaders) {
        Map<String, Long> nodeIdToTaskId = new HashMap<>();

        for(NodeDTO node : templateData.getNodeList()){
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

        for(ParticipantDTO leader : projectLeaders){
            String deptName = userQueryService.getDeptNameByUserId(leader.getUserId());
            Long deptId = deptQueryService.findDeptIdByName(deptName);
            long roleTeamLeaderId = roleService.findRoleByName("TEAM_LEADER");
            if (taskDeptIds.contains(deptId)) {
                matchedLeaders.add(
                        ParticipantDTO.builder()
                                .targetId(taskId)
                                .userId(leader.getUserId())
                                .targetType(TargetType.TASK)
                                .roleId(roleTeamLeaderId)
                                .build()
                );
            }
        }
        if (!matchedLeaders.isEmpty()) {
            createParticipants(matchedLeaders);
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

        for(Long deptId : deptIds){
            workDeptService.createWorkDept(taskId, deptId);
        }
        return taskId;
    }

    //
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
        log.info("태스크 및 태스트별 관계 설정 완료");


        // ✅ 5. 쓰기 작업 (deptId 기준)
        for (Long deptId : requestTaskDTO.getDeptList()) {
            String deptName = deptIdMap.get(deptId);

            workDeptService.createWorkDept(taskId, deptId);
            log.info("작업 별 참여 부서 생성 완료");

            List<Long> newParticipantsIds = deptUsersMaps.get(deptName);
            List<Long> leaderIds = deptLeaderMaps.get(deptName);
            long roleTeamLeaderId = roleService.findRoleByName("TEAM_LEADER");
            long roleTeamMemberId = roleService.findRoleByName("TEAM_MEMBER");
            // ✅ 1. 팀장 먼저 등록
            List<ParticipantDTO> leaderParticipants = leaderIds.stream()
                    .map(leaderId -> new ParticipantDTO(taskId, leaderId, TargetType.TASK, roleTeamLeaderId))
                    .toList();
            for (ParticipantDTO leader : leaderParticipants) {
                participantService.createParticipants(leader);
                participantNotification(leader);
            }
            log.info("팀장 등록 완료");

            // ✅ 2. 팀원 등록 (디렉터 & 팀장 제외)
            List<ParticipantDTO> teamParticipants = newParticipantsIds.stream()
                    .filter(participantUserId -> !leaderIds.contains(userId))       // 팀장 제외
                    .filter(participantUserId -> !userId.equals(directorId))        // 디렉터 제외
                    .map(participantUserId -> new ParticipantDTO(taskId, userId, TargetType.TASK, roleTeamMemberId))
                    .toList();
            for (ParticipantDTO teamParticipant : teamParticipants) {
                participantService.createParticipants(teamParticipant);
                participantNotification(teamParticipant);
            }
            log.info("팀원 등록 완료");
        }
        return taskId;
    }

    @Transactional
    public Long updateStatusProgress(Long taskId, Long userId) {

        Long projectId = taskQueryService.getProjectId(taskId);
        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);

        if (!isParticipant) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
        Long updateTaskId = taskService.updateStatusProgress(taskId);
        return updateTaskId;
    }

    @Transactional
    public Long updateStatusComplete(Long taskId, Long userId) {
        Long projectId = taskQueryService.getProjectId(taskId);
        boolean isProjectComplete = participantQueryService.isAboveTeamLeader(userId, projectId);
        if (!isProjectComplete) {
            throw new BaseException(ErrorCode.ACCESS_DENIED_TEAMLEADER);
        }
        Long updateTaskId = taskService.updateStatusComplete(taskId);

        // 후행 태스크에 참여하는 사용자에게 알림 전송
        List<Long> userIds = participantQueryService.findNextTaskUsersByTaskId(taskId);

        String taskName =  taskQueryService.getTaskName(taskId);

        // 각 사용자에게 알림 전송
        for (Long id : userIds) {
            notificationService.sendNotification(userId, "선행 태스크["+ taskName + "]가 완료되었습니다.", updateTaskId, NotificationTargetType.WORK);
        }

        return updateTaskId;
    }

    @Transactional
    public Long deleteTaskBySoft(Long taskId, Long userId) {
        Long projectId = taskQueryService.getProjectId(taskId);
        boolean isProjectComplete = participantQueryService.isAboveTeamLeader(userId, projectId);
        if (!isProjectComplete) {
            throw new BaseException(ErrorCode.ACCESS_DENIED_TEAMLEADER);
        }
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
                .targetId(detailId)
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
                    .targetId(detailId)  // 해당 세부일정의 ID
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
        long taskId = detailService.updateDetail(detailId, requestDetailDTO);

        long roleId = roleService.findRoleByName("ASSIGNEE");

        // 책임자DTO 생성해서 수정
        if (requestDetailDTO.getAssigneeId() != null) {
            ParticipantDTO assigneeDTO = ParticipantDTO.builder()
                    .targetType(TargetType.DETAILED)
                    .targetId(detailId)
                    .userId(requestDetailDTO.getAssigneeId())
                    .roleId(roleId)  // 담당자 역할 ID
                    .build();
            participantService.updateAssignee(detailId, assigneeDTO);  // 담당자 수정
        }

        // 참여자 수정
        if (requestDetailDTO.getParticipantIds() != null && !requestDetailDTO.getParticipantIds().isEmpty()) {
            participantService.updateParticipants(detailId, requestDetailDTO.getParticipantIds());  // 참여자 수정
        }

        // 부서 수정
        if (requestDetailDTO.getDeptId() != null) {
            workDeptService.updateWorkDept(detailId, requestDetailDTO.getDeptId()); // 부서 수정
        }

        //세부일정 진척률기반으로 태스크/프로젝트 진척률 자동업데이트
        updateProgressRate(taskId);

        return detailId;
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
                        .targetId(projectId)
                        .userId(leaderId)
                        .targetType(TargetType.PROJECT)
                        .roleId(2L)
                        .build()
                ).toList();
        createParticipants(leaders);

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
                        .targetId(projectId)
                        .userId(leaderId)
                        .targetType(TargetType.PROJECT)
                        .roleId(3L)
                        .build()
                ).toList();
        createParticipants(teamMember);

        // 초대 됐다는 알림 작성
        String writerName = userQueryService.getUserId(userId);
        String projectName = projectQueryService.getProjectName(projectId);
        String content = String.format("%s 님이 회원님을 %s에 초대하였습니다.", writerName, projectName);
        Long notificationId = notificationService.createInviteProject(projectId, content);
        notificationRecipientsService.createRecipients(participantUser, notificationId);
    }

    @Transactional
    public DelayInfoDTO delayAndPropagate(Long taskId, Integer delayDays, boolean isSimulate) {
        log.info("taskID: " + taskId);
        Map<Long, Integer> visited = new HashMap<>();
        Map<Long, Integer> realDelayed = new HashMap<>();
        Queue<DelayNodeDTO> queue = new LinkedList<>();
        Integer count = 0;

        Set<LocalDate> holidays = holidayQueryService.getHolidays().stream()
                .map(HolidayQueryDto::getDate).collect(Collectors.toSet());

        // 지연된 태스크 ID를 추적할 Set
        Set<Long> delayedTaskIds = new HashSet<>();

        Work startTask = taskService.findById(taskId);
        Project project = projectService.findById(startTask.getProjectId());

        if (isSimulate) {
            em.detach(startTask);
            em.detach(project);
        }

        LocalDate projectEndExpect = project.getEndExpect();
        LocalDate originProjectEndExpect = projectEndExpect;
        System.out.println("projectEndExpect = " + projectEndExpect);

        int delayDaysByTask = 0;

        // 지연 전파
        queue.offer(new DelayNodeDTO(taskId, delayDays));
        int startTaskDelay = Math.abs(delayDays - startTask.getSlackTime());

        while (!queue.isEmpty()) {
            DelayNodeDTO currentNode = queue.poll();

            // 현재 taskId에 대해 visited보다 작은 지연일일 경우 스킵
            Integer visitedDelay = visited.get(currentNode.getTaskId());
            if (visitedDelay != null && visitedDelay > currentNode.getDelayDays()) {
                continue;
            }

            // 현재 태스크 지연 설정
            Work currentTask = taskService.findById(currentNode.getTaskId());
            if (isSimulate) {
                em.detach(currentTask);
            }

            // 현재 태스크의 지연일
            int delayToApply = currentNode.getDelayDays();

            // 현재 태스크의 슬랙타임 및 지연일 설정
            if (currentTask.getSlackTime() >= delayToApply) {
                currentTask.setSlackTime(currentTask.getSlackTime() - delayToApply);
                if (Objects.equals(currentTask.getId(), taskId)) {
                    currentTask.setDelayDays(delayToApply);
                }
                currentTask.setEndExpect(currentTask.getEndExpect().plusDays(
                        taskService.calculateDelayExcludingHolidays(currentTask.getEndExpect(), delayDays, holidays)
                ));
            } else {
                log.info("슬랙타임 내에서 해결 실패");
                count++;
                int realDelay = delayToApply - currentTask.getSlackTime();
                realDelayed.put(currentTask.getId(), realDelay);
                System.out.println("delayToApply = " + delayToApply);
                System.out.println("slackTime = " + currentTask.getSlackTime());
                System.out.println("realDelay: " + realDelay);
                System.out.println("taskId = " + currentTask.getId());
                if (Objects.equals(currentTask.getId(), taskId)) {
                    currentTask.setDelayDays(currentTask.getDelayDays() + realDelay);   // 지연일 업데이트는 초기노드만 수정 필요
                }
                currentTask.setSlackTime(0);

                // 예상 마감일이 변경될 경우만 추적
                LocalDate originalEndExpect = currentTask.getEndExpect();

                // 현재 태스크와 세부일정 예상 마감일 미루기
                projectEndExpect=taskService.delayTask(currentTask, realDelay, holidays, projectEndExpect, isSimulate);

                // 예상 종료일이 변경된 경우에만 태스크ID 추가
                if (!currentTask.getEndExpect().equals(originalEndExpect)) {
                    delayedTaskIds.add(currentTask.getId());  // 실제로 마감일이 변경된 태스크만 추가
                    // 지연된 태스크에 참여한 인원에게 알림 보내기
                    List<Long> participants = participantQueryService.findParticipantsByTaskId(currentTask.getId());
                    String contents = "태스크 [" + startTask.getName() + "]가 지연되어 ["+ currentTask.getName() +"]의 예상마감일이 변경되었습니다!";
                    for (Long userId : participants) {
                        notificationService.sendNotification(userId, contents, currentTask.getId(), NotificationTargetType.WORK);
                    }
                }

                // 다음 노드에 realDelay를 전파
                List<Long> nextTaskIds = relationQueryService.findNextTaskIds(currentNode.getTaskId());

                for (Long nextTaskId  : nextTaskIds) {
                    // 다음 노드에 저장된 지연일
                    Integer storedDelay = visited.get(nextTaskId);
                    if (storedDelay == null || realDelay > storedDelay) {
                        visited.put(nextTaskId, realDelay);
                        queue.offer(new DelayNodeDTO(nextTaskId, realDelay));
                    }
                }
            }
            if (!isSimulate) {
                taskService.taskSave(currentTask);
            }
        }

        // 프로젝트 예상 마감일 업데이트
        if (project.getEndExpect().isBefore(projectEndExpect)) {
            Long projectDelay = ChronoUnit.DAYS.between(project.getEndExpect(), projectEndExpect)
                    -holidayQueryService.countHolidaysBetween(project.getEndExpect(), projectEndExpect);
            // 프로젝트 지연일수 업데이트
            // 해당 지연으로 밀린 프로젝트 지연일
            delayDaysByTask = Math.toIntExact(projectDelay);
            project.setEndExpect(projectEndExpect);
            project.setDelayDays(project.getDelayDays() + delayDaysByTask);


            if (!isSimulate) {
                projectService.projectSave(project);
            }

            // 프로젝트 마감일이 변경되었으면, 프로젝트 디렉터에게 알림 보내기
            Long directorUserId = participantQueryService.findDirectorByProjectId(project.getId());
            String directorContent = "프로젝트 [" + project.getName() + "]의 예상 마감일이 지연되었습니다!";
            notificationService.sendNotification(directorUserId, directorContent, project.getId(), NotificationTargetType.PROJECT);
        }

        visited.put(startTask.getId(), startTaskDelay);

        // 지연된 태스크 ID 출력
        System.out.println("지연된 태스크 ID들: " + delayedTaskIds);

        // 반환할 값

        log.info("기존 프로젝트 예상 마감일: {}", originProjectEndExpect);
        log.info("전체 지연일: {}", delayDaysByTask);
        log.info("지연 반영 된 프로젝트 마감일: {}", projectEndExpect);
        log.info("결재 요청 온 현재 태스크 지연일:{}", startTaskDelay);
        log.info("지연된 태스크 갯수:{}", count);
        log.info("영향 받은 태스크 id별 지연일:{}", visited);


        return DelayInfoDTO.builder()
                .originProjectEndExpect(originProjectEndExpect)
                .newProjectEndExpect(projectEndExpect)
                .delayDaysByTask(delayDaysByTask)
                .taskCountByDelay(count)
                .delayDaysByTaskId(realDelayed)
                .build();
    }


    // 프로젝트 분석 리포트 다운로드
    @Transactional
    public void downloadReport(Long projectId, HttpServletResponse response) {
        if (!projectService.isCompleted(projectId)) {
            throw new BaseException(ErrorCode.PROJECT_NOT_COMPLETED);
        }
        // 설명. 데이터 준비
        // 프로젝트 기본 정보
        ProjectDetailResponseDTO projectDetail = projectQueryService.getProjectDetail(projectId);
        // 참여자 정보 다 가져오기
        List<ProjectParticipantDTO> projectParticipantList = participantQueryService.getProjectParticipantList(projectId);

        // 공정 내역 - 태스트 조회
        List<CompletedTaskDTO> completedTaskList = taskQueryService.selectCompletedTasks(projectId);

        // 지연 사유서 내역
        List<ProjectApprovalDTO> delayList = approvalQueryService.selectProjectApprovalByProjectId(projectId, ApprovalType.DELAY);

        // 전체 프로젝트 정보 가져오기
        // 완료 상태인 프로젝트 전체 목록 가져오기
        List<CompletedProjectDTO> completedProjectList = projectQueryService.selectCompletedProjects();
        // 프로젝트별 태스크 리스트 가져오기

        // 각 프로젝트의 OTD 계산하기
        List<ProjectOTD> projectOTDList = taskQueryService.calculateProjectDTO(completedProjectList);
        for(ProjectOTD projectOTD : projectOTDList) {
            System.out.println(projectOTD.getProjectName() + " " + projectOTD.getOtdRate());
        }

        // 산출물 목록
        List<ReportAttachmentDTO> attachmentList = attachmentQueryService.getAttachmentsByProjectId(projectId);

        // PDF 생성
        pdfService.createReportPdf(response, projectDetail, projectParticipantList, completedTaskList, delayList, projectOTDList, attachmentList);


    }

    /* 설명. 디테일 수정 -> 읽기부터 하고 그 다음부터 수정 하나씩 */
    public Long updateTaskDetail(RequestModifyTaskDTO requestModifyTaskDTO, Long userId, Long taskId) {
        log.info(requestModifyTaskDTO.toString());
        Long projectId = requestModifyTaskDTO.getProjectId();
        boolean isAboveTeamLeader = participantQueryService.isAboveTeamLeader(userId, projectId);

        if (!isAboveTeamLeader) {
            throw new BaseException(ErrorCode.ACCESS_DENIED_TEAMLEADER);
        }
        /* 설명. 부서 id 조회 */
        List<Long> deptIds = new ArrayList<>();
        for( String deptName : requestModifyTaskDTO.getDeptLists()) {
            Long deptId = deptQueryService.findDeptIdByName(deptName);
            log.info(deptId.toString());
            deptIds.add(deptId);
        }

        /* 설명. 기존 작업 - 부서 관계 삭제 */
        workDeptService.deleteAllByTaskId(taskId);

        /* 설명. 기존 작업 별 관계 삭제 */
        if (requestModifyTaskDTO.getPrevTaskList() != null ||
                !requestModifyTaskDTO.getPrevTaskList().isEmpty()) {
            relationService.deleteByNextWorkId(taskId);
        }

        if (requestModifyTaskDTO.getNextTaskList() != null ||
                !requestModifyTaskDTO.getNextTaskList().isEmpty()) {
            relationService.deleteByPrevWorkId(taskId);
        }

        /* 설명. 기존 꺼 수정해서 save */
        Long modifyTaskId = taskService.modifyTaskDetail(requestModifyTaskDTO, taskId);
        /* 설명. 작업 별 관계, 부서 - 작업 별 관계 새로 삽입 */
        relationService.appendRelation(taskId,
                requestModifyTaskDTO.getPrevTaskList(),
                requestModifyTaskDTO.getNextTaskList());

        for (Long newDeptId : deptIds){
            workDeptService.createWorkDept(taskId, newDeptId);
        }

        return modifyTaskId;
    }

    public String findTaskNameById(long taskId) {
        return taskService.findTaskNameById(taskId);
    }

    private void createParticipants(List<ParticipantDTO> taskParticipants) {

        long roleId = roleService.findRoleByName("TEAM_LEADER");

        for (ParticipantDTO participant : taskParticipants) {
            participantService.createParticipants(participant);
            if (participant.getRoleId() == roleId) {
                String content = "";
                if (participant.getTargetType() == TargetType.TASK) { // TARGET TYPE이 TASK일 때
                    // WORK 테이블에서 태스크 이름 조회
                    String taskName = workQueryService.findTaskNameByTaskId(participant.getTargetId());
                    content = "태스크 [" + taskName + "]에 팀장으로 초대되었습니다.";
                    // 알림 전송
                    notificationService.sendNotification(participant.getUserId(), content, participant.getTargetId(), WORK);
                } else if (participant.getTargetType() == TargetType.PROJECT) { // TARGET TYPE이 PROJECT일 때
                    // PROJECT 테이블에서 프로젝트 이름 조회
                    String projectName = projectQueryService.findProjectNameByProjectId(participant.getTargetId());
                    content = "프로젝트 [" + projectName + "]에 팀장으로 초대되었습니다.";
                    notificationService.sendNotification(participant.getUserId(), content, participant.getTargetId(), PROJECT);
                }
            }
        }
    }
}
