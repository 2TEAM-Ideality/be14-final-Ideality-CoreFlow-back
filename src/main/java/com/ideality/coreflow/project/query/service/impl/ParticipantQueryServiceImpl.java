package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.project.query.mapper.ParticipantMapper;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantQueryServiceImpl implements ParticipantQueryService {
    private final ParticipantMapper participantMapper;

    @Override
    public Long selectDirectorByProjectId(Long projectId) {
        return participantMapper.selectDirectorByProjectId(projectId);
    }
}
