package com.ideality.coreflow.project.query.service;


import com.ideality.coreflow.project.query.dto.*;

import java.util.List;

public interface WorkQueryService {

    //세부일정 이름 목록을 반환
    List<String> getSubTaskNamesByParentTaskId(Long parentTaskId);

    //세부일정 이름 목록을 반환
    List<DetailDTO> getSubTaskDetailsByParentTaskId(Long parentTaskId);

    WorkDetailDTO getWorkDetailById(Long workId);

    List<DeptWorkDTO> selectWorksByDeptId(Long deptId);

    List<DetailMentionDTO> getDetailList(Long projectId, Long taskId, String detailTarget);

    List<Long> selectWorkIdByName(List<String> details);

    List<TaskProgressDTO> getDetailProgressByTaskId(Long taskId);

    List<Long> selectWorkIdsByParentTaskId(Long parentTaskId);
}
