package com.ideality.coreflow.project.command.application.service;

import java.util.List;

public interface RelationService {
    void appendRelation(List<Long> prevWorkId, Long nextWorkId);

    void appendMiddleRelation(List<Long> source, List<Long> target, Long taskId);

    void createRelation(Long taskId, Long nextWorkId);
}
