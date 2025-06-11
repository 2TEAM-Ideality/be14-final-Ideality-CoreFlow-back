package com.ideality.coreflow.project.query.service.facade;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.query.dto.*;
import com.ideality.coreflow.org.query.service.DeptQueryService;
import com.ideality.coreflow.project.query.service.*;
import com.ideality.coreflow.user.query.service.UserQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

        workDeptQueryService.selectDeptList(tasks);
        return tasks;
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
}
