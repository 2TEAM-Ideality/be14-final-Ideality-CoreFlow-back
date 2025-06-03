package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.project.query.dto.DetailDTO;
import com.ideality.coreflow.project.query.mapper.WorkMapper;
import com.ideality.coreflow.project.query.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}