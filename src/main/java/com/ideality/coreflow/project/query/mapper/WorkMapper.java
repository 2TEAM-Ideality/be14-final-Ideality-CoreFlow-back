package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.DeptWorkDTO;
import com.ideality.coreflow.project.query.dto.DetailDTO;
import com.ideality.coreflow.project.query.dto.ParticipantDTO;
import com.ideality.coreflow.project.query.dto.TaskProgressDTO;
import com.ideality.coreflow.project.query.dto.WorkDetailDTO;
import org.apache.ibatis.annotations.Mapper;
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

    List<String> selectDetailListByTarget(Long projectId, Long taskId, String detailTarget);

    List<Long> selectWorkIdByName(List<String> details);

    List<TaskProgressDTO> selectDetailProgressByTaskId(Long taskId);
}