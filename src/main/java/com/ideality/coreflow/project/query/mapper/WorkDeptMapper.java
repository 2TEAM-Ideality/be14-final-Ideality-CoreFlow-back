package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.TaskDeptDTO;
import com.ideality.coreflow.project.query.dto.WorkDeptDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkDeptMapper {
    List<WorkDeptDTO> selectDeptListMap(List<Long> taskIds);
}
