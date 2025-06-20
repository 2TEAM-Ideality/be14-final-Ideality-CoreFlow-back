package com.ideality.coreflow.project.query.service.facade;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.application.service.WorkService;
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
import com.ideality.coreflow.user.query.dto.UserNameIdDto;
import com.ideality.coreflow.user.query.dto.AllUserDTO;
import com.ideality.coreflow.user.query.service.UserQueryService;

import com.sun.jna.platform.win32.Netapi32Util.UserInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public ResponseTaskInfoDTO selectTaskInfo(Long taskId) {
        ResponseTaskInfoDTO selectTask =
                taskQueryService.selectTaskInfo(taskId);

        log.info("selectTaskInfo: {}", selectTask);

        relationQueryService.selectPrevRelation(taskId, selectTask);
        relationQueryService.selectNextRelation(taskId, selectTask);
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

        List<ParticipantDepartmentDTO> dto = participantQueryService.selectParticipantCountByDept(projectId);
        return dto;
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
}
