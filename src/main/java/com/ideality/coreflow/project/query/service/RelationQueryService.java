package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.query.dto.ResponseTaskInfoDTO;

public interface RelationQueryService {
    void selectRelation(Long taskId, ResponseTaskInfoDTO selectTask);
}
