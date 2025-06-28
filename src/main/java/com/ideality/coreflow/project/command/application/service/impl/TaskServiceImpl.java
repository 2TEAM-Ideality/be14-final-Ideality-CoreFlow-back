package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.dto.RequestModifyTaskDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.WorkRepository;
import com.ideality.coreflow.project.query.dto.WorkDueTodayDTO;
import com.ideality.coreflow.project.query.service.WorkQueryService;

import java.time.LocalDate;
import java.util.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import static com.ideality.coreflow.common.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final WorkRepository workRepository;

    @PersistenceContext
    private EntityManager em;

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
                .delayDays(0)
                .build();
        workRepository.saveAndFlush(taskWork);
        log.info("Task created with id {}", taskWork.getId());
        return taskWork.getId();
    }


    @Override
    @Transactional
    public Long updateStatusProgress(Long taskId) {
        Work updatedTask = workRepository.findById(taskId)
                .orElseThrow(() -> new BaseException(TASK_NOT_FOUND));


        if (updatedTask.getStatus() == Status.PROGRESS) {
            throw new BaseException(ErrorCode.INVALID_STATUS_PROGRESS);
        }
        updatedTask.startTask();
        return updatedTask.getId();
    }

    @Override
    @Transactional
    public Long updateStatusComplete(Long taskId) {
        Work updatedTask = workRepository.findById(taskId)
                .orElseThrow(() -> new BaseException(TASK_NOT_FOUND));

        if (updatedTask.getProgressRate() != 100) {
            throw new BaseException(TASK_PROGRESS_NOT_COMPLETED);
        }

        if (updatedTask.getStatus() == Status.COMPLETED) {
            throw new BaseException(ErrorCode.INVALID_STATUS_COMPLETED);
        }

        if (updatedTask.getStatus() == Status.PENDING) {
           throw new BaseException(ErrorCode.INVALID_STATUS_PENDING);
        }

        updatedTask.endTask();

        return updatedTask.getId();
    }

    @Override
    @Transactional
    public Long softDeleteTask(Long taskId) {
        Work deleteTask = workRepository.findById(taskId)
                .orElseThrow(() -> new BaseException(TASK_NOT_FOUND));

        if (deleteTask.getStatus() == Status.DELETED) {
            throw new BaseException(ErrorCode.INVALID_STATUS_DELETED);
        }
        deleteTask.softDeleteTask();
        return deleteTask.getId();
    }

    @Override
    public void validateRelation(List<Long> source) {
        if (source == null) {
            throw new BaseException(INVALID_SOURCE_LIST);
        }

        for (Long sourceId : source) {
            if (sourceId == null || !workRepository.existsById(sourceId)) {
                throw new BaseException(TASK_NOT_FOUND);
            }
        }
    }

    @Override
    public void validateTask(Long taskId) {
        if (!workRepository.existsById(taskId)) {
            throw new BaseException(TASK_NOT_FOUND);
        }
    }

    @Override
    public void updateProgressRate(Long taskId, double progress) {
        Work work = findById(taskId);
        work.updateProgressRate(progress);
    }

    @Override
    public String findTaskNameById(long taskId) {
        return workRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND)).getName();
    }

    @Override
    public void delayWork(Work work, Integer delayDays, Set<LocalDate> holidays, boolean isSimulate) {
        if (work.getStatus() == Status.PENDING) {
            work.setStartExpect(work.getStartExpect().plusDays(
                    calculateDelayExcludingHolidays(work.getStartExpect(), delayDays, holidays)
            ));
        }
        if (work.getStatus() == Status.PENDING || work.getStatus() == Status.PROGRESS) {
            work.setEndExpect(work.getEndExpect().plusDays(
                    calculateDelayExcludingHolidays(work.getEndExpect(), delayDays, holidays)
            ));
        }
        if (!isSimulate) {
            workRepository.save(work);
        }
    }

    @Override
    public int calculateDelayExcludingHolidays(LocalDate startDate, Integer delayDays, Set<LocalDate> holidays) {
        int addedDays = 0;
        int workingDays = 0;
        LocalDate date = startDate;

        while (workingDays < delayDays) {
            date = date.plusDays(1);
            if(!holidays.contains(date)) {
                workingDays++;
            }
            addedDays++;
        }
        return addedDays;
    }

    @Override
    @Transactional
    public Long modifyTaskDetail(RequestModifyTaskDTO requestModifyTaskDTO, Long taskId, Status status) {
        Work modifyTask =
                workRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));

        modifyTask.updateTaskDetail(
                requestModifyTaskDTO.getTaskName(),
                requestModifyTaskDTO.getDescription(),
                requestModifyTaskDTO.getStartExpect(),
                requestModifyTaskDTO.getEndExpect());
        if (status == Status.PENDING) {
            modifyTask.updateBaseLine(requestModifyTaskDTO.getStartExpect(), requestModifyTaskDTO.getEndExpect());
        }
        return modifyTask.getId();
    }

    @Override
    public Work findById(Long taskId) {
        return workRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));
    }

    @Override
    public void setTaskWarning(Long taskId, Boolean warning) {
        Work task = workRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));
        task.setWarning(warning);
        workRepository.save(task);
    }
}
