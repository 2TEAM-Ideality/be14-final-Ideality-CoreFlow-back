package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.SelectTaskDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskMapper {

    SelectTaskDTO selectTaskInfo(Long taskId);

    List<ResponseTaskDTO> selectTasks(Long projectId);

    Long selectProjectIdByTaskId(Long taskId);
}
