package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.CheckTaskWarningDTO;
import com.ideality.coreflow.project.query.dto.CompletedTaskDTO;
import com.ideality.coreflow.project.query.dto.GanttTaskResponse;
import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.SelectTaskDTO;
import com.ideality.coreflow.project.query.dto.TaskProgressDTO;
import com.ideality.coreflow.project.query.dto.WorkDueTodayDTO;
import java.time.LocalDate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TaskMapper {

    SelectTaskDTO selectTaskInfo(Long taskId);

    List<ResponseTaskDTO> selectTasks(Long projectId);

    Long selectProjectIdByTaskId(Long taskId);

    int countIncompleteTasks(Long projectId);

    String selectTaskNameByTaskId(Long taskId);

	List<CompletedTaskDTO> selectCompletedTasks(Long projectId);

    List<TaskProgressDTO> selectTaskProgressByProjectId(Long projectId);

    List<GanttTaskResponse> selectRootTasksByProjectId(Long projectId);

    List<GanttTaskResponse> selectSubTasksByParentId(Long parentId);

    List<WorkDueTodayDTO> findWorksDueToday(@Param("projectIds") List<Long> projectIds,
                                            @Param("today") LocalDate today);

    CheckTaskWarningDTO findTaskEndExpect(Long taskId);
}
