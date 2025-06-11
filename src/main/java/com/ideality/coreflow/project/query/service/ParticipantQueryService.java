package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.query.dto.DepartmentLeaderDTO;
import com.ideality.coreflow.project.query.dto.ParticipantDepartmentDTO;
import com.ideality.coreflow.project.query.dto.ResponseParticipantDTO;

import java.util.List;

public interface ParticipantQueryService {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);

    List<Long> selectParticipantsList(Long detailParticipantId);

    boolean isParticipant(Long userId, Long projectId);

    List<ParticipantDepartmentDTO> selectParticipantCountByDept(Long projectId);

    List<DepartmentLeaderDTO> selectTeamLeaderByDepartment(Long projectId);

    List<ResponseParticipantDTO> selectParticipantsByDeptName(Long projectId, String deptName);
}
