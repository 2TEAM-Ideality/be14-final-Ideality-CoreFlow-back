package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;
import com.ideality.coreflow.template.query.dto.EdgeDTO;

import java.util.List;


public interface TaskQueryService {
    ResponseTaskInfoDTO selectTaskInfo(Long taskId);

    List<ResponseTaskDTO> selectTasks(Long projectId);

    List<EdgeDTO> getEdgeList(List<ResponseTaskDTO> taskList);

    Long getProjectId(Long taskId);
}
