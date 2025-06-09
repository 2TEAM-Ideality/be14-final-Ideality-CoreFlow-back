package com.ideality.coreflow.project.query.service;

public interface ParticipantQueryService {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);
}
