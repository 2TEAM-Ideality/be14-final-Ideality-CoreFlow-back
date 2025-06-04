package com.ideality.coreflow.project.query.mapper;

import java.util.List;

import com.ideality.coreflow.project.query.dto.RelationDTO;
import com.ideality.coreflow.project.query.dto.prevTaskDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RelationMapper {
    prevTaskDTO selectRelation(Long taskId);

	List<RelationDTO> findAllRelationsForTaskIds(List<Long> taskIds);
}
