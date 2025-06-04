package com.ideality.coreflow.project.query.service.facade;

import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;
import com.ideality.coreflow.project.query.service.RelationQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.project.query.service.WorkDeptQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectQueryFacadeService {

    private final TaskQueryService taskQueryService;
    private final RelationQueryService relationQueryService;
    private final WorkDeptQueryService workDeptQueryService;

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
}
