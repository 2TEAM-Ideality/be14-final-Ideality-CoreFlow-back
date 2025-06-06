package com.ideality.coreflow.project.query.service;


import com.ideality.coreflow.project.query.dto.DeptWorkDTO;
import com.ideality.coreflow.project.query.dto.DetailDTO;
import com.ideality.coreflow.project.query.dto.WorkDetailDTO;

import java.util.List;

public interface WorkService {
    // parent_task_id가 동일한 세부일정 이름 목록을 반환
    List<String> getSubTaskNamesByParentTaskId(Long parentTaskId);

    List<DetailDTO> getSubTaskDetailsByParentTaskId(Long parentTaskId);

    WorkDetailDTO getWorkDetailById(Long workId);

    List<DeptWorkDTO> selectWorksByDeptId(Long deptId);

    List<String> getDetailList(Long projectId, Long taskId, String detailTarget);
}
