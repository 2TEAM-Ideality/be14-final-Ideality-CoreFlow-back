package com.ideality.coreflow.project.query.mapper;

import java.util.List;

import com.ideality.coreflow.project.query.dto.NextTaskDTO;
import com.ideality.coreflow.project.query.dto.RelationDTO;
import com.ideality.coreflow.project.query.dto.PrevTaskDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RelationMapper {
	PrevTaskDTO selectRelation(Long taskId);

	List<RelationDTO> findAllRelationsForTaskIds(List<Long> taskIds);

    List<PrevTaskDTO> selectPrevRelation(Long taskId);

    List<NextTaskDTO> selectNextRelation(Long taskId);
}
