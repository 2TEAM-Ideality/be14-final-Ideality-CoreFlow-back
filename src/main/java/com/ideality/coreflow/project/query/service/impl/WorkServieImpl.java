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

        // 참여자 리스트에서 assignee와 participant로 나누기
        List<ParticipantDTO> assignees = new ArrayList<>();
        List<ParticipantDTO> participantList = new ArrayList<>();

        for (ParticipantDTO participant : participants) {
            if (participant.getRoleId() == 6) {
                assignees.add(participant); // roleId가 6이면 assignee로 추가
            } else if (participant.getRoleId() == 7) {
                participantList.add(participant); // roleId가 7이면 participant로 추가
            }
        }

        // 작업 상세에 assignee, participant 정보 설정
        workDetail.setAssignees(assignees); // assignee 리스트 설정
        workDetail.setParticipants(participantList); // participant 리스트 설정

        // prevWorkIds에 해당하는 work의 name을 가져오기
        List<String> prevWorkNames = workMapper.findWorkNamesByIds(workDetail.getPrevWorkIds());
        workDetail.setPrevWorkNames(prevWorkNames); // workDetail에 prevWorkNames 설정

        // nextWorkIds에 해당하는 work의 name을 가져오기
        List<String> nextWorkNames = workMapper.findWorkNamesByIds(workDetail.getNextWorkIds());
        workDetail.setNextWorkNames(nextWorkNames); // workDetail에 nextWorkNames 설정

        return workDetail;
    }

    // 부서 아이디로 해당 부서의 세부일정 목록 조회
    @Override
    public List<DeptWorkDTO> selectWorksByDeptId(Long deptId) {

        // 부서별 세부일정 목록 조회
		return workMapper.findWorkListByDeptId(deptId);
    }
}