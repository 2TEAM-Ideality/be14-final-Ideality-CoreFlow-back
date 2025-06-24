package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.DelayInfoDTO;
import com.ideality.coreflow.project.command.application.dto.RequestModifyTaskDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;


import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.query.dto.WorkDueTodayDTO;
import java.util.List;

public interface TaskService {
    Long createTask(RequestTaskDTO taskDTO);

    Long updateStatusProgress(Long taskId);

    Long updateStatusComplete(Long taskId);

    Long softDeleteTask(Long taskId);

    void validateRelation(List<Long> source);

    void validateTask(Long taskId);

    Double updateTaskProgress(Long taskId);

    DelayInfoDTO delayAndPropagate(Long taskId, Integer delayDays, boolean isSimulate);

    String findTaskNameById(long taskId);

//    Double updateTaskProgress(Long taskId, List<TaskProgressDTO> workList);

    Long modifyTaskDetail(RequestModifyTaskDTO requestModifyTaskDTO, Long taskId);

    void setTaskWarning(Long taskId, Boolean warning);

    List<WorkDueTodayDTO> getWorksDueToday(List<Long> projectIds);
}
