package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.command.domain.repository.TaskRepository;
import com.ideality.coreflow.project.query.dto.RelationDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;
import com.ideality.coreflow.project.query.dto.SelectTaskDTO;
import com.ideality.coreflow.project.query.dto.TaskProgressDTO;
import com.ideality.coreflow.project.query.mapper.RelationMapper;
import com.ideality.coreflow.project.query.mapper.TaskMapper;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.template.query.dto.EdgeDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskQueryServiceImpl implements TaskQueryService {
    private final TaskMapper taskMapper;
    private final RelationMapper relationMapper;
    private final TaskRepository taskRepository;

    @Override
    public ResponseTaskInfoDTO selectTaskInfo(Long taskId) {
        SelectTaskDTO dto = taskMapper.selectTaskInfo(taskId);
        ResponseTaskInfoDTO resDto = new ResponseTaskInfoDTO();
        resDto.setSelectTask(dto);
        return resDto;
    }

    @Override
    public List<ResponseTaskDTO> selectTasks(Long projectId) {
        return taskMapper.selectTasks(projectId);
    }

    @Override
    public List<EdgeDTO> getEdgeList(List<ResponseTaskDTO> taskList) {
        // 1. 태스크 ID 추출
        List<Long> taskIds = taskList.stream()
            .map(ResponseTaskDTO::getId)
            .toList();

        // 2. relation 테이블에서 관련된 모든 관계 조회
        List<RelationDTO> relations = relationMapper.findAllRelationsForTaskIds(taskIds);

        // 3. EdgeDTO로 변환
        return relations.stream()
            .filter(rel -> taskIds.contains(rel.getPrevWorkId()) && taskIds.contains(rel.getNextWorkId()))
            .map(rel -> EdgeDTO.builder()
                .id("e" + rel.getPrevWorkId() + "-" + rel.getNextWorkId())
                .source(String.valueOf(rel.getPrevWorkId()))
                .target(String.valueOf(rel.getNextWorkId()))
                .type("default")
                .build())
            .toList();
    }

    @Override
    public Long getProjectId(Long taskId) {
        return taskMapper.selectProjectIdByTaskId(taskId);
    }

    @Override
    public boolean isAllTaskCompleted(Long projectId) {
        return taskMapper.countIncompleteTasks(projectId)==0;
    }

    @Override
    public Long selectProjectIdByTaskId(Long taskId) {
        Long projectId = taskMapper.selectProjectIdByTaskId(taskId);

        if (projectId == null) {
            throw new BaseException(ErrorCode.TASK_NOT_FOUND);
        }
        return projectId;
    }

    @Override
    public String getTaskName(Long taskId) {
        return taskMapper.selectTaskNameByTaskId(taskId);
    }

    @Override
    public List<TaskProgressDTO> getTaskProgressByProjectId(Long projectId) {
        return taskMapper.selectTaskProgressByProjectId(projectId);
    }
}
