package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.query.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WorkMapper {

    //task_id가 동일한 세부일정 목록 조회
    List<String> findSubTaskNamesByParentTaskId(Long parentTaskId);

    // parent_task_id가 동일한 세부일정과 담당 부서 정보를 조회
    List<DetailDTO> findSubTaskDetailsByParentTaskId(Long parentTaskId);

    // 작업 ID로 세부 일정을 조회
    WorkDetailDTO findWorkDetailById(Long workId);

    List<ParticipantDTO> findParticipantsByWorkId(Long participantId);

    List<String> findWorkNamesByIds(List<Long> workIds);

	List<DeptWorkDTO> findWorkListByDeptId(Long deptId);

    List<DetailMentionDTO> selectDetailListByTarget(Long projectId, Long taskId, String detailTarget);

    String findTaskNameByTaskId(Long targetId);

    List<Long> selectWorkIdByName(List<String> details);

    List<WorkProgressDTO> selectDetailProgressByTaskId(Long taskId);

    // 오늘 마감일인 진행 중인 작업 조회
    List<Work> findTasksDueToday(LocalDate today);

    // 내일 마감일인 진행 중인 작업 조회
    List<Work> findTasksDueTomorrow(LocalDate tomorrow);

    List<Long> selectWorkIdsByParentTaskId(Long parentTaskId);

    List<Work> selectTaskByParentTaskId(Long parentTaskId);
}