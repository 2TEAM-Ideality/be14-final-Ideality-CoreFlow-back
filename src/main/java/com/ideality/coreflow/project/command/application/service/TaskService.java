package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;

import java.util.List;

public interface TaskService {
    Long createTask(RequestTaskDTO taskDTO);

    Long updateStatusProgress(Long taskId);

    Long updateStatusComplete(Long taskId);

    Long softDeleteTask(Long taskId);

    void validateRelation(List<Long> source);

    void validateTask(Long taskId);

    Double updateTaskProgress(Long taskId);
}
