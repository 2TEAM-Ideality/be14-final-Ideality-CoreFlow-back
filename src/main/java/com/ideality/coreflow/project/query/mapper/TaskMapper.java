package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.SelectTaskDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper {

    SelectTaskDTO selectTaskInfo(Long taskId);
}
