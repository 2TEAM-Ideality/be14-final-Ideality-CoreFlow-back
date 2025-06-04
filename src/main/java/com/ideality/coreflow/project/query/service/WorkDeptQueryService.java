package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;

import java.util.List;

public interface WorkDeptQueryService {
    void selectDeptList(List<ResponseTaskDTO> tasks);
}
