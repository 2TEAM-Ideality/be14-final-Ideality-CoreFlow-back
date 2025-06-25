package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.query.dto.*;
import com.ideality.coreflow.project.query.dto.CompletedTaskDTO;
import com.ideality.coreflow.project.query.dto.GanttTaskResponse;
import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;
import com.ideality.coreflow.template.query.dto.EdgeDTO;

import java.util.List;


public interface TaskQueryService {
    ResponseTaskInfoDTO selectTaskInfo(Long taskId);

    List<ResponseTaskDTO> selectTasks(Long projectId);

    List<CompletedTaskDTO> selectCompletedTasks(Long projectId);

    List<ProjectOTD> calculateProjectDTO(List<CompletedProjectDTO> completedProjectList);

    List<EdgeDTO> getEdgeList(List<ResponseTaskDTO> taskList);

    Long getProjectId(Long taskId);

    boolean isAllTaskCompleted(Long projectId);

    Long selectProjectIdByTaskId(Long taskId);

    String getTaskName(Long taskId);

    List<WorkProgressDTO> getTaskProgressByProjectId(Long projectId);

    void sendTaskDueReminder();

    List<GanttTaskResponse> getGanttTasksByProjectId(Long projectId);

    Boolean checkTaskWarning(Long taskId);

    List<WorkDueTodayDTO> getWorksDueToday(List<Long> projectIds, Long userId);
}
