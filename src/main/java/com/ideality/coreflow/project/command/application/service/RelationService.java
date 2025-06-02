package com.ideality.coreflow.project.command.application.service;

public interface RelationService {
    void appendRelation(Long prevWorkId, Long nextWorkId, Long taskId);
}
