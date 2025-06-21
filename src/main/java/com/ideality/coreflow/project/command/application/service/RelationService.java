package com.ideality.coreflow.project.command.application.service;

import java.util.List;

public interface RelationService {

    void appendMiddleRelation(List<Long> source, List<Long> target, Long taskId);


    void appendTargetRelation(List<Long> target, Long taskId);
//    void updateRelations(Long detailId, List<Long> source, List<Long> target);

    void createRelation(Long taskId, Long nextWorkId);

    void appendRelation(Long taskId, List<Long> source, List<Long> target);

    void deleteByNextWorkId(Long taskId);

    void deleteByPrevWorkId(Long taskId);
}
