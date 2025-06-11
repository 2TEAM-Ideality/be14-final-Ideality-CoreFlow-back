package com.ideality.coreflow.project.query.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ParticipantMapper {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);

    List<Long> selectParticipantsList(Long detailParticipantId);

    boolean isParticipantUser(Long userId, Long projectId);

    boolean isTeamLeader(Long projectId, String deptName);

    boolean isInviteRole(Long projectId, Long userId);

    boolean isAlreadyParticipant(Long projectId, Long userId, String deptName);

    List<Long> selectParticipantUserId(Long projectId);
}
