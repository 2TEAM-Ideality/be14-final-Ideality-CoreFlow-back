package com.ideality.coreflow.project.query.service.impl;

import java.util.List;

import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
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

    @Override
    public boolean isProjectDirector(Long projectId, Long userId) {
        return participantMapper.isProjectDirector(projectId, userId);
    }

    @Override
    public List<ParticipantDTO> getParticipantList(Long projectId) {
        return List.of();
    }
}
