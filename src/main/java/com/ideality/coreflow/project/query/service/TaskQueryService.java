package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;


public interface TaskQueryService {
    ResponseTaskInfoDTO selectTaskInfo(Long taskId);

}
