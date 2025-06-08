package com.ideality.coreflow.project.query.service;

import java.util.List;

public interface ParticipantQueryService {
    Long selectDirectorByProjectId(Long projectId);

    List<Long> selectParticipantsList(Long detailParticipantId);
}
