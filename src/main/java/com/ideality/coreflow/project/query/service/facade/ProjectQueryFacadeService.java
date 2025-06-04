package com.ideality.coreflow.project.query.service.facade;

import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;
import com.ideality.coreflow.project.query.dto.SelectTaskDTO;
import com.ideality.coreflow.project.query.service.RelationQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectQueryFacadeService {

    private final TaskQueryService taskQueryService;
    private final RelationQueryService relationQueryService;

    public ResponseTaskInfoDTO selectTaskInfo(Long taskId) {
        ResponseTaskInfoDTO selectTask =
                taskQueryService.selectTaskInfo(taskId);

        relationQueryService.selectRelation(taskId, selectTask);
        return selectTask;
    }
}
