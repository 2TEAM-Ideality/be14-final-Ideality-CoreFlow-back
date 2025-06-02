package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;

public interface TaskService {
    Long createTask(RequestTaskDTO taskDTO);

    void validateWorkId(Long prevWorkId, Long nextWorkId);
}
