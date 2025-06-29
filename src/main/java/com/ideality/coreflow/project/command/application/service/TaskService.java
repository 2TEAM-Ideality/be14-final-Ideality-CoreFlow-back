package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.RequestModifyTaskDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.Work;

import java.time.LocalDate;
import com.ideality.coreflow.project.query.dto.WorkDueTodayDTO;
import java.util.List;
import java.util.Set;

public interface TaskService {
    Long createTask(RequestTaskDTO taskDTO);

    Long updateStatusProgress(Long taskId);

    Long updateStatusComplete(Long taskId);

    Long softDeleteTask(Long taskId);

    void validateRelation(List<Long> source);

    void validateTask(Long taskId);

    void updateProgressRate(Long taskId, double progress);

    String findTaskNameById(long taskId);

//    Double updateTaskProgress(Long taskId, List<TaskProgressDTO> workList);

    Long modifyTaskDetail(RequestModifyTaskDTO requestModifyTaskDTO, Long taskId, Status status);

    Work findById(Long taskId);

    void delayWork(Work work, Integer delayDays, Set<LocalDate> holidays, boolean isSimulate);

    int calculateDelayExcludingHolidays(LocalDate startDate, Integer delayDays, Set<LocalDate> holidays);

    void setTaskWarning(Long taskId, Boolean warning);

    void updateSlackTime(Long taskId, Integer slack);

    void updateStatusCancelled(Long taskId);

    void deleteTaskHard(Long taskId);

    void updateStatusPending(Long taskId);
}
