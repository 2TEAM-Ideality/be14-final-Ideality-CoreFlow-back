package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.project.query.dto.prevTaskDTO;
import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;
import com.ideality.coreflow.project.query.mapper.RelationMapper;
import com.ideality.coreflow.project.query.service.RelationQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RelationQueryServiceImpl implements RelationQueryService {
    private final RelationMapper relationMapper;


    @Override
    public void selectRelation(Long taskId, ResponseTaskInfoDTO selectTask) {
        prevTaskDTO relation = relationMapper.selectRelation(taskId);
//        selectTask.setRelation(relation);
    }
}
