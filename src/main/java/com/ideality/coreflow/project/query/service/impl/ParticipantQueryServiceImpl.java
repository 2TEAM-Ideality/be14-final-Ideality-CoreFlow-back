package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.project.query.dto.ParticipantDepartmentDTO;
import com.ideality.coreflow.project.query.mapper.ParticipantMapper;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<Long> selectParticipantsList(Long detailParticipantId) {
        return participantMapper.selectParticipantsList(detailParticipantId);
    }

    @Override
    public boolean isParticipant(Long userId, Long projectId) {
        return participantMapper.isParticipantUser(userId, projectId);
    }

    @Override
    public List<ParticipantDepartmentDTO> selectParticipantCountByDept(Long projectId) {
        return participantMapper.selectParticipantCountByDept(projectId);
    }
}
