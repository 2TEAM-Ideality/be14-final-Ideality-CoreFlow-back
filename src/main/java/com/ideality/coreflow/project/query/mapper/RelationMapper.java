package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.NextTaskDTO;
import com.ideality.coreflow.project.query.dto.PrevTaskDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RelationMapper {

    List<PrevTaskDTO> selectPrevRelation(Long taskId);

    List<NextTaskDTO> selectNextRelation(Long taskId);
}
