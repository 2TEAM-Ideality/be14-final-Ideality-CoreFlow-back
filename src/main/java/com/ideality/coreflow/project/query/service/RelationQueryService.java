package com.ideality.coreflow.project.query.service;


import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;

public interface RelationQueryService {
    void selectPrevRelation(Long taskId, ResponseTaskInfoDTO selectTask);

    void selectNextRelation(Long taskId, ResponseTaskInfoDTO selectTask);
}
