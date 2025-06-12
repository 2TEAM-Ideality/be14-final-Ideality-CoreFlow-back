package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.project.query.dto.NextTaskDTO;
import com.ideality.coreflow.project.query.dto.PrevTaskDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;
import com.ideality.coreflow.project.query.mapper.RelationMapper;
import com.ideality.coreflow.project.query.service.RelationQueryService;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RelationQueryServiceImpl implements RelationQueryService {
    private final RelationMapper relationMapper;


    @Override
    public void selectPrevRelation(Long taskId, ResponseTaskInfoDTO selectTask) {
        List<PrevTaskDTO> prevSet = relationMapper.selectPrevRelation(taskId);
        selectTask.setPrevTasks(prevSet);
    }

    @Override
    public void selectNextRelation(Long taskId, ResponseTaskInfoDTO selectTask) {
        List<NextTaskDTO> nextSet = relationMapper.selectNextRelation(taskId);
        selectTask.setNextTasks(nextSet);
    }

    @Override
    public List<Long> findNextTaskIds(Long taskId) {
        return relationMapper.selectNextTaskIds(taskId);
    }
}
