package com.ideality.coreflow.project.query.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ParticipantMapper {
    Long selectDirectorByProjectId(Long projectId);

    List<Long> selectParticipantsList(Long detailParticipantId);
}
