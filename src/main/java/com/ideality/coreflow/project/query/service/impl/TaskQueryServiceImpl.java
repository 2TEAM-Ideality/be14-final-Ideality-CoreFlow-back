package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;
import com.ideality.coreflow.project.query.dto.SelectTaskDTO;
import com.ideality.coreflow.project.query.mapper.TaskMapper;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskQueryServiceImpl implements TaskQueryService {
    private final TaskMapper taskMapper;

    @Override
    public ResponseTaskInfoDTO selectTaskInfo(Long taskId) {
//        SelectTaskDTO dto = taskMapper.selectTaskInfo(taskId);
//        ResponseTaskInfoDTO resDto = new ResponseTaskInfoDTO(dto);
        return null;
    }

    @Override
    public List<ResponseTaskDTO> selectTasks(Long projectId) {
        return taskMapper.selectTasks(projectId);
    }
}
