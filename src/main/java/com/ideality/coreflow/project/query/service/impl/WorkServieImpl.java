package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.project.query.dto.DeptWorkDTO;
import com.ideality.coreflow.project.query.dto.DetailDTO;
import com.ideality.coreflow.project.query.dto.ParticipantDTO;
import com.ideality.coreflow.project.query.dto.WorkDetailDTO;
import com.ideality.coreflow.project.query.mapper.WorkMapper;
import com.ideality.coreflow.project.query.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkServieImpl implements WorkService {

    private final WorkMapper workMapper;

    // parent_task_id가 동일한 세부일정 이름 목록을 반환
    @Override
    public List<String> getSubTaskNamesByParentTaskId(Long parentTaskId) {
            return workMapper.findSubTaskNamesByParentTaskId(parentTaskId);
        }

    // parent_task_id에 해당하는 세부 일정과 담당 부서 정보를 조회
    @Override
    public List<DetailDTO> getSubTaskDetailsByParentTaskId(Long parentTaskId) {
        return workMapper.findSubTaskDetailsByParentTaskId(parentTaskId);
    }

    // 작업 상세 정보 조회
    @Override
    public WorkDetailDTO getWorkDetailById(Long workId) {
        WorkDetailDTO workDetail = workMapper.findWorkDetailById(workId);

        // 선행 및 후행 일정 ID들을 List<Long>으로 변환
        workDetail.setPrevWorkIds();
        workDetail.setNextWorkIds();

        // 참여자 정보 매핑
        List<ParticipantDTO> participants = workMapper.findParticipantsByWorkId(workId);

        workDetail.setParticipants(participants != null ? participants : new ArrayList<ParticipantDTO>());  // 빈 리스트 처리

        return workDetail;
    }

    // 부서 아이디로 해당 부서의 세부일정 목록 조회
    @Override
    public List<DeptWorkDTO> selectWorksByDeptId(Long deptId) {
        List<DeptWorkDTO> deptList = workMapper.findWorkListByDeptId(deptId);

        return deptList;
    }
}