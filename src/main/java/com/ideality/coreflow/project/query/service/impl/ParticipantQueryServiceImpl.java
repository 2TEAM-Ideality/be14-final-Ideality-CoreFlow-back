package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.project.query.dto.DepartmentLeaderDTO;
import com.ideality.coreflow.project.query.dto.ParticipantDepartmentDTO;
import com.ideality.coreflow.project.query.dto.ResponseParticipantDTO;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.command.application.dto.RequestInviteUserDTO;
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

    @Override
    public List<DepartmentLeaderDTO> selectTeamLeaderByDepartment(Long projectId) {
        return participantMapper.selectTeamLeaderByDepartment(projectId);
    }

    @Override
    public List<ResponseParticipantDTO> selectParticipantsByDeptName(Long projectId, String deptName) {
        return participantMapper.selectParticipantByDeptName(projectId, deptName);
    }

    @Override
    public void findTeamLedaer(Long projectId, List<RequestInviteUserDTO> reqLeaderDTO) {
        for (RequestInviteUserDTO leaderDTO : reqLeaderDTO) {
            boolean isLeaderAlreadyExists = participantMapper.isTeamLeader
                            (projectId, leaderDTO.getDeptName());

            if (isLeaderAlreadyExists) {
                throw new BaseException(ErrorCode.TEAM_LEADER_ALREADY_EXISTS);
            }
        }
    }

    @Override
    public boolean isAboveTeamLeader(Long userId, Long projectId) {
        return participantMapper.isAboveTeamLeader(projectId, userId);
    }

    @Override
    public void alreadyExistsMember(Long projectId, List<RequestInviteUserDTO> reqMemberDTO) {
        for (RequestInviteUserDTO userDTO : reqMemberDTO) {
            boolean isAlreadyParticipantExists =
                    participantMapper.isAlreadyParticipant(projectId, userDTO.getUserId(), userDTO.getDeptName());

            if (isAlreadyParticipantExists) {
                throw new BaseException(ErrorCode.TEAM_MEMBER_ALREADY_EXISTS);
            }
        }
    }

    @Override
    public List<Long> selectParticipantUserId(Long projectId) {
        return participantMapper.selectParticipantUserId(projectId);
    }
}
