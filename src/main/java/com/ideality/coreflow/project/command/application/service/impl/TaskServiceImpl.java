package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.ideality.coreflow.common.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public Long createTask(RequestTaskDTO taskDTO) {
        Work taskWork = Work.builder()
                .name(taskDTO.getLabel())
                .description(taskDTO.getDescription())
                .createdAt(LocalDateTime.now())
                .startBase(taskDTO.getStartBaseLine())
                .endBase(taskDTO.getEndBaseLine())
                .startExpect(taskDTO.getStartBaseLine())
                .endExpect(taskDTO.getEndBaseLine())
                .status(Status.PENDING)
                .projectId(taskDTO.getProjectId())
                .build();
        taskRepository.saveAndFlush(taskWork);
        log.info("Task created with id {}", taskWork.getId());
        return taskWork.getId();
    }


    @Override
    @Transactional
    public Long updateStatusProgress(Long taskId) {
        Work updatedTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new BaseException(TASK_NOT_FOUND));

        updatedTask.startTask();
        return updatedTask.getId();
    }

    @Override
    @Transactional
    public Long updateStatusComplete(Long taskId) {
        Work updatedTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new BaseException(TASK_NOT_FOUND));

        if (updatedTask.getProgressRate() != 100) {
            throw new BaseException(TASK_PROGRESS_NOT_COMPLETED);
        }

        updatedTask.endTask();
        return updatedTask.getId();
    }

    @Override
    @Transactional
    public Long softDeleteTask(Long taskId) {
        Work deleteTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new BaseException(TASK_NOT_FOUND));

        deleteTask.softDeleteTask();
        return deleteTask.getId();
    }

    @Override
    public void validateSource(List<Long> source) {
        if (source.isEmpty()) {
            throw new BaseException(INVALID_SOURCE_LIST);
        }

        if (source.contains(null)){
            throw new BaseException(INVALID_SOURCE_LIST);
        }

        for (Long sourceId : source) {
            if (sourceId != 0 && !taskRepository.existsById(sourceId)) {
                throw new BaseException(TASK_NOT_FOUND);
            }
        }
    }

    @Override
    public void validateTarget(List<Long> target) {
        if (target.isEmpty()) {
            throw new BaseException(INVALID_SOURCE_LIST);
        }

        for (Long targetId : target) {
            if (!taskRepository.existsById(targetId)) {
                throw new BaseException(TASK_NOT_FOUND);
            }
        }
    }
}
