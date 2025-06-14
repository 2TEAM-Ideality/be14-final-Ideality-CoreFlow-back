package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.query.dto.TaskPreviewDTO;

import java.util.List;
import java.util.Map;

public interface WorkService {
    Double updatePassedRate(Long workId);

    Map<Long, List<TaskPreviewDTO>> findByProjectIdIn(List<Long> projectIds);
}
