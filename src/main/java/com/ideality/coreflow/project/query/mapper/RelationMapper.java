package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.prevTaskDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RelationMapper {
    prevTaskDTO selectRelation(Long taskId);
}
