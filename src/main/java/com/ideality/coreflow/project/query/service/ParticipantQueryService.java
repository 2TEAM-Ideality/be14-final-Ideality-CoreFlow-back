package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.command.application.dto.RequestInviteUserDTO;

import com.ideality.coreflow.project.query.dto.DepartmentLeaderDTO;
import com.ideality.coreflow.project.query.dto.ParticipantDepartmentDTO;
import com.ideality.coreflow.project.query.dto.ResponseParticipantDTO;
import com.ideality.coreflow.project.query.dto.ResponseParticipantUser;
import com.ideality.coreflow.user.query.dto.UserNameIdDto;

import java.util.List;
import java.util.Map;

public interface ParticipantQueryService {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);

    void findTeamLedaer(Long projectId, List<RequestInviteUserDTO> reqLeaderDTO);

    List<Long> selectParticipantsList(Long detailParticipantId);

    boolean isParticipant(Long userId, Long projectId);

    boolean isAboveTeamLeader(Long userId, Long projectId);

    void alreadyExistsMember(Long projectId, List<RequestInviteUserDTO> reqMemberDTO);

    List<Long> selectParticipantUserId(Long projectId);

    List<ParticipantDepartmentDTO> selectParticipantCountByDept(Long projectId);

    List<DepartmentLeaderDTO> selectTeamLeaderByDepartment(Long projectId);

    List<ResponseParticipantDTO> selectParticipantsByDeptName(Long projectId, String deptName);

    Map<Long, List<ResponseParticipantUser>> findByParticipantsIn(List<Long> projectIds);
}
