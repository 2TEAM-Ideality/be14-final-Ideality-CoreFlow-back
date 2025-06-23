package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.notification.command.application.service.NotificationService;
import com.ideality.coreflow.project.command.application.dto.RequestModifyTaskDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.WorkRepository;
import com.ideality.coreflow.project.query.dto.TaskProgressDTO;
import com.ideality.coreflow.project.query.mapper.ParticipantMapper;
import com.ideality.coreflow.notification.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.project.query.service.WorkQueryService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    private final HolidayQueryService holidayQueryService;

    private final ParticipantMapper participantMapper;
    private final NotificationService notificationService;
    private final TaskQueryService taskQueryService;

    private final ProjectService projectService;
    private final WorkQueryService workQueryService;
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

        // 후행 태스크에 참여하는 사용자에게 알림 전송
        List<Long> userIds = participantMapper.findNextTaskUsersByTaskId(taskId);

        String taskName =  taskQueryService.getTaskName(taskId);

        // 각 사용자에게 알림 전송
        for (Long userId : userIds) {
            notificationService.sendNotification(userId, "선행 태스크["+ taskName + "]가 완료되었습니다.", updatedTask.getId(), TargetType.WORK);
        }

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
    public Long updateTaskProgress(Long taskId) {
        List<TaskProgressDTO> workList = workQueryService.getDetailProgressByTaskId(taskId);
        Work task = workRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));
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
        workRepository.saveAndFlush(task);

        return task.getProjectId();
    }

    @Override
    public String findTaskNameById(long taskId) {
        return workRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND)).getName();
    }


    @Override
    public LocalDate delayTask(Work task, Integer delayDays, Set<LocalDate> holidays, LocalDate projectEndExpect, boolean isSimulate) {
        if (task.getStatus() == Status.PENDING) {
            task.setStartExpect(task.getStartExpect().plusDays(
                    calculateDelayExcludingHolidays(task.getStartExpect(), delayDays, holidays)
            ));
        }
        task.setEndExpect(task.getEndExpect().plusDays(
                calculateDelayExcludingHolidays(task.getEndExpect(), delayDays, holidays)
        ));
        System.out.println("task.getId() = " + task.getId());
        System.out.println("task.getEndExpect() = " + task.getEndExpect());
        if(projectEndExpect.isBefore(task.getEndExpect())) {
            projectEndExpect = task.getEndExpect();
        }
        System.out.println("projectEndExpect = " + projectEndExpect);
        if (!isSimulate) {
            workRepository.save(task);
        }

        // 태스크 하위 세부일정들도 지연 처리
        // 하위 세부일정들 불러오기
        List<Long> detailIds = workQueryService.selectWorkIdsByParentTaskId(task.getId());
        // 하위 세부일정 지연 처리
        if (detailIds != null) {
            for (Long detailId : detailIds) {
                Work detailWork = workRepository.findById(detailId).orElseThrow(() -> new BaseException(WORK_NOT_FOUND));
                if (isSimulate) {
                    em.detach(detailWork);
                }
                delayWork(detailWork, delayDays, holidays, isSimulate);
            }
        }
        return projectEndExpect;
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
    public void taskSave(Work currentTask) {
        workRepository.save(currentTask);
    }

    @Override
    @Transactional
    public Long modifyTaskDetail(RequestModifyTaskDTO requestModifyTaskDTO, Long taskId) {
        Work modifyTask =
                workRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));

        modifyTask.updateTaskDetail(requestModifyTaskDTO.getDescription(),
                requestModifyTaskDTO.getStartExpect(),
                requestModifyTaskDTO.getEndExpect());
        return modifyTask.getId();
    }

    @Override
    public Work findById(Long taskId) {
        return workRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));
    }
}
