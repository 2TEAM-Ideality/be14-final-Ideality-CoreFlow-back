package com.ideality.coreflow.project.query.service.facade;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.application.service.WorkService;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.query.dto.*;
import com.ideality.coreflow.project.query.dto.CompletedProjectDTO;
import com.ideality.coreflow.project.query.dto.DepartmentLeaderDTO;
import com.ideality.coreflow.project.query.dto.DeptWorkDTO;
import com.ideality.coreflow.project.query.dto.ParticipantDepartmentDTO;
import com.ideality.coreflow.project.query.dto.ProjectDetailResponseDTO;
import com.ideality.coreflow.project.query.dto.PipelineResponseDTO;
import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;
import com.ideality.coreflow.project.query.dto.ResponseInvitableUserDTO;
import com.ideality.coreflow.project.query.dto.ResponseParticipantDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;
import com.ideality.coreflow.org.query.service.DeptQueryService;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import com.ideality.coreflow.project.query.service.ProjectQueryService;
import com.ideality.coreflow.project.query.service.RelationQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.project.query.service.WorkDeptQueryService;
import com.ideality.coreflow.project.query.service.WorkQueryService;
import com.ideality.coreflow.user.query.dto.AllUserDTO;
import com.ideality.coreflow.user.query.service.UserQueryService;

import com.sun.jna.platform.win32.Netapi32Util.UserInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectQueryFacadeService {

    private final ProjectService projectService;

    private final TaskQueryService taskQueryService;
    private final UserQueryService userQueryService;
    private final DeptQueryService deptQueryService;
    private final RelationQueryService relationQueryService;
    private final WorkQueryService workService;
    private final WorkDeptQueryService workDeptQueryService;
    private final ProjectQueryService projectQueryService;
    private final ParticipantQueryService participantQueryService;
    private final TaskService taskService;
    private final WorkService workCommandService;


    public TaskSummaryResponse getTodayTaskSummary(Long userId) {
        List<Long> projectIds = projectQueryService.getProjectIdsInProgressByUser(userId);

        if (projectIds.isEmpty()) {
            return TaskSummaryResponse.builder()
                    .taskCount(0)
                    .detailCount(0)
                    .tasks(Collections.emptyList())
                    .subtasks(Collections.emptyList())
                    .build();
        }

        LocalDate today = LocalDate.now();
        List<WorkDueTodayDTO> works = taskQueryService.getWorksDueToday(projectIds);

        List<WorkSummaryDTO> tasks = works.stream()
                .filter(w -> "TASK".equals(w.getType()))
                .map(w -> new WorkSummaryDTO(w.getWorkId(), w.getName()))
                .toList();

        List<WorkSummaryDTO> subtasks = works.stream()
                .filter(w -> "SUBTASK".equals(w.getType()))
                .map(w -> new WorkSummaryDTO(w.getWorkId(), w.getName()))
                .toList();

        return TaskSummaryResponse.builder()
                .taskCount(tasks.size())
                .detailCount(subtasks.size())
                .tasks(tasks)
                .subtasks(subtasks)
                .build();
    }
//    public TaskSummaryResponse getTodayTaskSummary(Long userId) {
//        // 1. 진행중인 프로젝트 ID 목록
//        List<Long> projectIds = projectQueryService.getProjectIdsInProgressByUser(userId);
//
//        // 2. 해당 프로젝트에서 오늘 마감인 모든 작업(work) 가져오기
//        List<Work> todayDueWorks = taskService.getWorksDueToday(projectIds);
//
//        // 3. 태스크 / 세부일정 분류
//        int taskCount = 0;
//        int detailCount = 0;
//
//        for (Work work : todayDueWorks) {
//            if (work.getParentTaskId() == null) taskCount++;
//            else detailCount++;
//        }
//
//        return new TaskSummaryResponse(taskCount, detailCount);
//    }

    public List<UserInfoDTO> getParticipants(Long projectId) {
        // 참여중인 모든 인원 호출
        List<UserInfoDTO> allParticipants = participantQueryService.getAllProjectParticipants(projectId);
        System.out.println("allParticipants = " + allParticipants);
        return allParticipants;
//        // 부서 목록 추출
//        Set<String> deptList = participantQueryService.extractDeptNamesFromParticipants(allParticipants);
//        // 데이터 조립
//        List<ParticipantTeamDTO> result = new ArrayList<>();
//        for(String dept: deptList) {
//            System.out.println("dept = " + dept);
//            ParticipantTeamDTO participantTeamDTO = ParticipantTeamDTO.builder()
//                    .deptName(dept)
//                    .teamLeader(allParticipants.stream()
//                            .filter(participant->participant.getDeptName().equals(dept))
//                            .findFirst()
//                            .orElse(null))
//                    .teamMembers(allParticipants.stream()
//                            .filter(participant->participant.getDeptName().equals(dept))
//                            .toList())
//                    .build();
//            System.out.println("participantTeamDTO = " + participantTeamDTO);
//            result.add(participantTeamDTO);
//        }
//        return result;
    }

    public List<GanttTaskResponse> getGanttChart(Long projectId) {
        return taskQueryService.getGanttTasksByProjectId(projectId);
    }


    public ProjectDetailResponseDTO getProjectDetail(Long projectId) {
        return projectQueryService.getProjectDetail(projectId);
    }

    public List<ProjectSummaryDTO> selectProjectSummaries(Long userId) {
        return projectQueryService.selectProjectSummaries(userId);
    }

    public ResponseTaskInfoDTO selectTaskInfo(Long taskId, Long userId) {

        /* 설명. 이전 태스크랑 이후 태스크에 대한 것은 배열로 정보를 가져와야 하므로, SelectTask
         *  조회해오고 부서 정보는 부서 정보, 이전 태스크 이후 태스크 정보는 각각에서 조합해와서 조합
        * */

        /* 설명. 프로젝트에 참여중인 사람만이 이 정보를 볼 수 있음(예외처리) */
        Long projectId = taskQueryService.selectProjectIdByTaskId(taskId);

        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);

        if (!isParticipant) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        ResponseTaskInfoDTO selectTask =
                taskQueryService.selectTaskInfo(taskId);

        log.info("selectTaskInfo: {}", selectTask);

        /* 설명: 마감 임박 세부일정 갯수 조회*/
        Long nearDueSubtaskCount = taskQueryService.getNearDueSubtaskCount(taskId);
        selectTask.setNearDueSubtaskCount(nearDueSubtaskCount);

        /* 설명. 관계 조회 */
        relationQueryService.selectPrevRelation(taskId, selectTask);
        relationQueryService.selectNextRelation(taskId, selectTask);

        /* 설명. 부서 이름 조회 */
        workDeptQueryService.selectDeptNamesByTask(taskId, selectTask);
        return selectTask;
    }

    public List<ResponseTaskDTO> selectTasks(Long projectId) {
        List<ResponseTaskDTO> tasks = taskQueryService.selectTasks(projectId);

        if (!tasks.isEmpty()) {
            workDeptQueryService.selectDeptList(tasks);
        }

        return tasks;
    }

    // 완료된 태스크 목록 조회
    public List<CompletedTaskDTO> selectCompletedTasks(Long projectId) {
        return taskQueryService.selectCompletedTasks(projectId);
    }

    // 부서별 세부일정 조회
    public List<DeptWorkDTO> selectWorksByDeptId(Long userId) {
        String deptName = userQueryService.getDeptNameByUserId(userId);
        Long deptId = deptQueryService.findDeptIdByName(deptName);
		return workService.selectWorksByDeptId(deptId);
    }

    // 오늘의 부서 일정 조회
    public List<DeptWorkDTO> selectTodayWorksByDeptId(Long userId) {
        String deptName = userQueryService.getDeptNameByUserId(userId);
        Long deptId = deptQueryService.findDeptIdByName(deptName);
        List<DeptWorkDTO> totalWorks = workService.selectWorksByDeptId(deptId);

        LocalDate today = LocalDate.now();

        return totalWorks.stream()
            .filter(work -> {
                LocalDate start = toLocalDate(work.getStartExpect());
                LocalDate end = toLocalDate(work.getEndExpect());

                // 오늘이 시작일과 종료일 사이에 포함되는 일정만
                return (start != null && end != null &&
                    ( !today.isBefore(start) && !today.isAfter(end) ));
            })
            .collect(Collectors.toList());
    }

    // 날짜 파싱
    private LocalDate toLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public PipelineResponseDTO getPipeline (Long projectId) {
        return projectQueryService.getPipeline(projectId);
    }


    public List<ResponseInvitableUserDTO> getInvitableUser(Long projectId, Long userId) {
        projectService.existsById(projectId);
        boolean isAboveTeamLeader
                = participantQueryService.isAboveTeamLeader(userId, projectId);
        if (!isAboveTeamLeader) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        List<AllUserDTO> userList = userQueryService.selectAllUser();
        List<Long> alreadyParticipantUser = participantQueryService.selectParticipantUserId(projectId);

        List<ResponseInvitableUserDTO> result = userList.stream()
                .map(user -> new ResponseInvitableUserDTO(
                        user.getId(),
                        user.getName(),
                        user.getDeptName(),
                        user.getJobRank(),
                        user.getProfileImage(),
                        alreadyParticipantUser.contains(user.getId())
                ))
                .toList();

        return result;
    }
    public List<ParticipantDepartmentDTO> getParticipantDepartment(Long projectId, Long userId) {
        projectService.existsById(projectId);
        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);
        if (!isParticipant) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        List<ParticipantDepartmentDTO> dtoList = participantQueryService.selectParticipantCountByDept(projectId);
        for (ParticipantDepartmentDTO dto : dtoList) {
            Long deptId = deptQueryService.findDeptIdByName(dto.getDeptName());
            dto.setDeptId(deptId);
        }
        return dtoList;
    }

    public List<DepartmentLeaderDTO> getTeamLeaderByDepartment(Long projectId, Long userId) {
        projectService.existsById(projectId);
        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);
        if (!isParticipant) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        List<DepartmentLeaderDTO> dto = participantQueryService.selectTeamLeaderByDepartment(projectId);
        return dto;
    }

    public List<ResponseParticipantDTO> getParticipantByDepartment(Long projectId, Long userId, String deptName) {
        projectService.existsById(projectId);
        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);
        if (!isParticipant) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        List<ResponseParticipantDTO> dto = participantQueryService.selectParticipantsByDeptName(projectId, deptName);
        return dto;
    }

    public Map<Long, List<TaskPreviewDTO>> selectTaskSummaries(List<Long> projectIds) {
        return workCommandService.findByProjectIdIn(projectIds);
    }

    public Map<Long, List<ResponseParticipantUser>> selectParticipantSummaries(List<Long> projectIds) {
        return participantQueryService.findByParticipantsIn(projectIds);
    }
    // 완료된 프로젝트 목록 조회
    public List<CompletedProjectDTO> getCompletedProjectList() {
        return projectQueryService.selectCompletedProjects();
    }

    // 부서별 참여 프로젝트 목록 조회
    public List<ProjectSummaryDTO> getProjectsByDeptId(RequestDeptDTO deptName) {
        // TODO. 부서 이름으로 부서 아이디 조회
        Long deptId = deptQueryService.findDeptIdByName(deptName.getDeptName());
        return projectQueryService.selectProjectByDeptId(deptId);
    }

    @Transactional
    public List<GanttTaskResponse> getGanttTasksByProjectId(Long projectId) {
        return taskQueryService.getGanttTasksByProjectId(projectId);
    }

    public ProjectDateDTO getGanttProjectDate(Long projectId) {
        return projectService.findProjectDateById(projectId);
    }

    // 태스크별 참여자 조회
    public List<ParticipantUserDTO> getTaskParticipant(Long taskId) {
        return participantQueryService.selectParticipantByTaskId(taskId);
    }
}
