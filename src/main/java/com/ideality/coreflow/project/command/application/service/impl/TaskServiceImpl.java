package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.dto.DelayNodeDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.RelationRepository;
import com.ideality.coreflow.project.command.domain.repository.TaskRepository;
import com.ideality.coreflow.project.query.dto.TaskProgressDTO;
import com.ideality.coreflow.project.query.service.RelationQueryService;
import com.ideality.coreflow.template.query.dto.NodeDTO;

import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
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
    private final RelationQueryService relationQueryService;

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

    @Override
    public void validateTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new BaseException(TASK_NOT_FOUND);
        }
    }

    @Override
    public Double updateTaskProgress(Long taskId, List<TaskProgressDTO> workList) {
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
        return task.getProgressRate();
    }

    @Override
    @Transactional
    public Integer delayAndPropagate(Long taskId, Integer delayDays) {
        Set<Long> visited = new HashSet<>();
        Queue<DelayNodeDTO> queue = new LinkedList<>();

        // 초기 태스크 처리
        Work startTask = taskRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));
        startTask.setEndExpect(startTask.getEndExpect().plusDays(delayDays));
        startTask.setDelayDays(startTask.getDelayDays() + delayDays);
        taskRepository.save(startTask);

        // 지연 전파
        queue.offer(new DelayNodeDTO(taskId, delayDays));

        while (!queue.isEmpty()) {
            DelayNodeDTO currentNode = queue.poll();
            List<Long> nextTaskIds = relationQueryService.findNextTaskIds(currentNode.getTaskId());

            for (Long nextTaskId : nextTaskIds) {
                if (visited.contains(nextTaskId)) {continue;}

                Work nextTask = taskRepository.findById(nextTaskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));
                Integer delayToApply = currentNode.getDelayDays();

                if (nextTask.getSlackTime() >= delayToApply) {
                    nextTask.setSlackTime(nextTask.getSlackTime() - delayToApply);
                    taskRepository.save(nextTask);
                }else {
                    int realDelay = delayToApply - nextTask.getSlackTime();
                    nextTask.setSlackTime(0);
                    delayTask(nextTask, realDelay);
                    queue.offer(new DelayNodeDTO(nextTaskId, realDelay));
                }
                visited.add(nextTaskId);
            }
        }
        return visited.size();
    }

    private void delayTask(Work task, Integer delayDays) {
        task.setStartExpect(task.getStartExpect().plusDays(delayDays));
        task.setEndExpect(task.getEndExpect().plusDays(delayDays));
        taskRepository.save(task);
    }
}
