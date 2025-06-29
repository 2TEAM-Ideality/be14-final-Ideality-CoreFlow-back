package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.project.query.dto.*;
import java.util.List;

import com.ideality.coreflow.project.query.dto.ProjectParticipantDTO;
import com.ideality.coreflow.project.query.dto.DepartmentLeaderDTO;
import com.ideality.coreflow.project.query.dto.ParticipantDepartmentDTO;
import com.ideality.coreflow.project.query.dto.ResponseParticipantDTO;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.command.application.dto.RequestInviteUserDTO;
import com.ideality.coreflow.project.query.mapper.ParticipantMapper;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;

import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

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
    public Map<Long, List<ResponseParticipantUser>> findByParticipantsIn(List<Long> projectIds) {
        List<ParticipantUserDTO> participant = participantMapper.selectAllUserByProjectIds(projectIds);

        return participant.stream()
                .collect(Collectors.groupingBy(
                        ParticipantUserDTO::getProjectId,
                        Collectors.mapping(
                            dto -> new ResponseParticipantUser(dto.getUserId(), dto.getName(), dto.getDeptName(), dto.getJobRankName(), dto.getJobRoleName()),
                            Collectors.toList()
                        )
                ));
    }

    @Override
    public List<UserInfoDTO> getAllProjectParticipants(Long projectId) {
        List<UserInfoDTO> result = participantMapper.selectAllUserByProjectId(projectId);
        return result.stream()
                .map(participant -> participant.toBuilder()
                        .roleId(participant.getRoleId().equals("1") ? "디렉터" :
                                participant.getRoleId().equals("2")? "팀장" : "팀원").build())
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> extractDeptNamesFromParticipants(List<UserInfoDTO> allParticipants) {
        return allParticipants.stream()
                .map(UserInfoDTO::getDeptName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Long> findParticipantsByTaskId(Long id) {
        return participantMapper.findParticipantsByTaskId(id);
    }

    @Override
    public Long findDirectorByProjectId(Long id) {
        return participantMapper.findDirectorByProjectId(id);
    }

    @Override
    public List<Long> findNextTaskUsersByTaskId(Long taskId) {
        return participantMapper.findNextTaskUsersByTaskId(taskId);
    }

    @Override
    public List<ParticipantUserDTO> selectParticipantByTaskId(Long taskId) {
        List<ParticipantUserDTO> result = participantMapper.selectParticipantByTaskId(taskId);
        return result.stream()
                .map(participant -> participant.toBuilder()
                        .roleId(participant.getRoleId().equals("1") ? "디렉터" :
                                participant.getRoleId().equals("2")? "팀장" : "팀원").build())
                .collect(Collectors.toList());
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

    @Override
    public List<ProjectParticipantDTO> getProjectParticipantList(Long projectId) {
        return participantMapper.selectProjectParticipantList(projectId);
    }
    @Override
    public void alreadyExistsTaskMember(Long taskId, List<RequestInviteUserDTO> reqMemberDTO) {
        for (RequestInviteUserDTO userDTO : reqMemberDTO) {
            boolean isAlreadyParticipantExists =
                    participantMapper.isAlreadyParticipantByTask(taskId, userDTO.getUserId(), userDTO.getDeptName());

            if (isAlreadyParticipantExists) {
                throw new BaseException(ErrorCode.TEAM_MEMBER_ALREADY_EXISTS);
            }
        }
    }
}
