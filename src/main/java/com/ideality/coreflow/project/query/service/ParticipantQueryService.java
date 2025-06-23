package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.command.application.dto.RequestInviteUserDTO;

import com.ideality.coreflow.project.query.dto.DepartmentLeaderDTO;
import com.ideality.coreflow.project.query.dto.ParticipantDepartmentDTO;
import com.ideality.coreflow.project.query.dto.ResponseParticipantDTO;
import com.ideality.coreflow.project.query.dto.ResponseParticipantUser;
import com.ideality.coreflow.project.query.dto.UserInfoDTO;
import com.ideality.coreflow.user.query.dto.UserNameIdDto;

import java.util.List;
import java.util.Map;

import com.ideality.coreflow.project.query.dto.ProjectParticipantDTO;
import java.util.Set;

public interface ParticipantQueryService {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);

	List<ProjectParticipantDTO> getProjectParticipantList(Long projectId);

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

    //  프로젝트 id로 해당 프로젝트에 참여하는 모든 인원들 정보 가져옴(유저아이디, 유저이름, 부서이름, 직책, 프로필사진)
    List<UserInfoDTO> getAllProjectParticipants(Long projectId);

    Set<String> extractDeptNamesFromParticipants(List<UserInfoDTO> allParticipants);

    List<Long> findParticipantsByTaskId(Long id);

    Long findDirectorByProjectId(Long id);
}
