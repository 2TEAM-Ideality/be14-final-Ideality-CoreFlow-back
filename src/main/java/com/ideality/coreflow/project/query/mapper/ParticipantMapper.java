package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.DepartmentLeaderDTO;
import com.ideality.coreflow.project.query.dto.ParticipantDepartmentDTO;
import com.ideality.coreflow.project.query.dto.ParticipantUserDTO;
import com.ideality.coreflow.project.query.dto.ResponseParticipantDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParticipantMapper {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);

    List<Long> selectParticipantsList(Long detailParticipantId);

    boolean isParticipantUser(Long userId, Long projectId);

    boolean isTeamLeader(Long projectId, String deptName);

    boolean isAboveTeamLeader(Long projectId, Long userId);

    boolean isAlreadyParticipant(Long projectId, Long userId, String deptName);

    List<Long> selectParticipantUserId(Long projectId);

    List<ParticipantDepartmentDTO> selectParticipantCountByDept(Long projectId);

    List<DepartmentLeaderDTO> selectTeamLeaderByDepartment(Long projectId);

    List<ResponseParticipantDTO> selectParticipantByDeptName(Long projectId, String deptName);

    List<ParticipantUserDTO> selectAllUserByProjectIds(@Param("projectIds") List<Long> projectIds);
}
