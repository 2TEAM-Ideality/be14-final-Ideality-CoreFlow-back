package com.ideality.coreflow.project.query.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParticipantMapper {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);
}
