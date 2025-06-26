package com.ideality.coreflow.project.query.service;


import com.ideality.coreflow.project.query.dto.*;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.query.dto.DeptWorkDTO;
import com.ideality.coreflow.project.query.dto.DetailDTO;
import com.ideality.coreflow.project.query.dto.WorkProgressDTO;
import com.ideality.coreflow.project.query.dto.WorkDetailDTO;

import java.util.List;

public interface WorkQueryService {

    //세부일정 이름 목록을 반환
    List<String> getSubTaskNamesByParentTaskId(Long parentTaskId);

    //세부일정 이름 목록을 반환
    List<DetailDTO> getSubTaskDetailsByParentTaskId(Long parentTaskId);

    WorkDetailDTO getWorkDetailById(Long workId);

    List<DeptWorkDTO> selectWorksByDeptId(Long deptId);

    List<DetailMentionDTO> getDetailList(Long projectId, Long taskId, String detailTarget);

    List<Long> selectWorkIdByName(List<String> details, Long taskId);

    List<WorkProgressDTO> getDetailProgressByTaskId(Long taskId);

    List<Long> selectWorkIdsByParentTaskId(Long parentTaskId);

    List<Work> getSubTasksByParentTaskId(Long id);

    String findTaskNameByTaskId(Long taskId);

    // 세부 일정 id가 주어졌을 때 부모 태스크의 id를 반환
    Long getParentTaskId(Long subTaskId);

    Boolean checkTaskWarning(Long subTaskId);

}
