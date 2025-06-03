package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.DetailDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface WorkMapper {

    //task_id가 동일한 세부일정 목록 조회
    List<String> findSubTaskNamesByParentTaskId(Long parentTaskId);

    // parent_task_id가 동일한 세부일정과 담당 부서 정보를 조회
    List<DetailDTO> findSubTaskDetailsByParentTaskId(Long parentTaskId);


}