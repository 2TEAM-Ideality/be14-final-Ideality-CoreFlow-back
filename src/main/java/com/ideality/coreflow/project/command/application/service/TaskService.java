package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.DelayInfoDTO;
import com.ideality.coreflow.project.command.application.dto.RequestModifyTaskDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.domain.aggregate.Work;


import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TaskService {
    Long createTask(RequestTaskDTO taskDTO);

    Long updateStatusProgress(Long taskId);

    Long updateStatusComplete(Long taskId);

    Long softDeleteTask(Long taskId);

    void validateRelation(List<Long> source);

    void validateTask(Long taskId);

    Long updateTaskProgress(Long taskId);

    String findTaskNameById(long taskId);

//    Double updateTaskProgress(Long taskId, List<TaskProgressDTO> workList);

    Long modifyTaskDetail(RequestModifyTaskDTO requestModifyTaskDTO, Long taskId);

    Work findById(Long taskId);

    LocalDate delayTask(Work task, Integer delayDays, Set<LocalDate> holidays, LocalDate projectEndExpect, boolean isSimulate);

    void delayWork(Work work, Integer delayDays, Set<LocalDate> holidays, boolean isSimulate);

    int calculateDelayExcludingHolidays(LocalDate startDate, Integer delayDays, Set<LocalDate> holidays);

    void taskSave(Work currentTask);
}
