package com.ideality.coreflow.project.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface WorkMapper {

    // parent_task_id(pinia 이용)가 동일한 세부일정 이름 목록 조회
    List<String> findSubTaskNamesByParentTaskId(Long parentTaskId);

}