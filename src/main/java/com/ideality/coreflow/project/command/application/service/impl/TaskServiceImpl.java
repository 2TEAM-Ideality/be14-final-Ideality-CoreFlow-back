package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.TaskRepository;
import com.ideality.coreflow.project.query.dto.TaskProgressDTO;
import com.ideality.coreflow.project.query.service.WorkQueryService;
import com.ideality.coreflow.template.query.dto.NodeDTO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private final HolidayQueryService holidayQueryService;
    private final ProjectService projectService;
    private final WorkQueryService workQueryService;

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
    public void validateRelation(List<Long> source) {
        if (source.isEmpty() || source.contains(null)) {
            throw new BaseException(INVALID_SOURCE_LIST);
        }
        for (Long sourceId : source) {
            if (!taskRepository.existsById(sourceId)) {
                throw new BaseException(TASK_NOT_FOUND);
            }
        }
    }

    @Override
    public void validateTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new BaseException(TASK_NOT_FOUND);
        }
    }

    @Override
    public Double updateTaskProgress(Long taskId) {
        List<TaskProgressDTO> workList = workQueryService.getDetailProgressByTaskId(taskId);
        Work task = taskRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));
        Long totalDuration = 0L;
        Double totalProgress = 0.0;
        for (TaskProgressDTO work : workList) {
            log.info(work.toString());
            Long duration = (ChronoUnit.DAYS.between(work.getStartDate(), work.getEndDate()) + 1
                    - holidayQueryService.countHolidaysBetween(work.getStartDate(), work.getEndDate()));
            totalDuration += duration;
            System.out.println("duration = " + duration);

            Double progress = duration * (work.getProgressRate()/100);
            System.out.println("progress = " + progress);
            totalProgress += progress;
        }
        System.out.println("Num to Save = " + Math.round(totalProgress/totalDuration*10000)/100.0);
        task.setProgressRate(Math.round(totalProgress/totalDuration*10000)/100.0);
        taskRepository.saveAndFlush(task);

        // 프로젝트 진척률도 수정
        projectService.updateProjectProgress(task.getProjectId());

        return task.getProgressRate();
    }
}
