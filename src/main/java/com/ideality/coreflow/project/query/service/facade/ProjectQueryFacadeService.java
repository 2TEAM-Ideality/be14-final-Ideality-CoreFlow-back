package com.ideality.coreflow.project.query.service.facade;

import com.ideality.coreflow.project.query.dto.DeptWorkDTO;
import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.project.query.service.ProjectQueryService;
import com.ideality.coreflow.project.query.service.RelationQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.project.query.service.WorkDeptQueryService;
import com.ideality.coreflow.project.query.service.WorkService;
import com.ideality.coreflow.user.query.service.UserQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectQueryFacadeService {

    private final TaskQueryService taskQueryService;
    private final UserQueryService userQueryService;
    private final DeptQueryService deptQueryService;
    private final RelationQueryService relationQueryService;
    private final WorkService workService;
    private final WorkDeptQueryService workDeptQueryService;
    private final ProjectQueryService projectQueryService;

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
}
