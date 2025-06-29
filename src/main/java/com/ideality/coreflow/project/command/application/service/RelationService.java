package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.EdgeInfoDTO;
import com.ideality.coreflow.project.command.application.dto.RequestRelationUpdateDTO;
import com.ideality.coreflow.template.query.dto.EdgeDTO;

import java.util.List;

public interface RelationService {

    void appendMiddleRelation(List<Long> source, List<Long> target, Long taskId);


    void appendTargetRelation(List<Long> target, Long taskId);
//    void updateRelations(Long detailId, List<Long> source, List<Long> target);

    void createRelation(Long taskId, Long nextWorkId);

    void appendRelation(Long taskId, List<Long> source, List<Long> target);

    void deleteByNextWorkId(Long taskId);

    void deleteByPrevWorkId(Long taskId);

    void updateRelationList(List<RequestRelationUpdateDTO> requestRelationUpdateDTO);

    List<EdgeInfoDTO> findTargetBySourceId(Long taskId);
}
