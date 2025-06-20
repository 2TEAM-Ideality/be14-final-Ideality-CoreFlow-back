package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;

import java.util.List;

public interface WorkDeptQueryService {
    void selectDeptList(List<ResponseTaskDTO> tasks);

    void selectDeptNamesByTask(Long taskId, ResponseTaskInfoDTO selectTask);
}
