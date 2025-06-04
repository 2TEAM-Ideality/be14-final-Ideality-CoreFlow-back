package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;

import java.util.List;


public interface TaskQueryService {
    ResponseTaskInfoDTO selectTaskInfo(Long taskId);

    List<ResponseTaskDTO> selectTasks(Long projectId);
}
