package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.DepartmentLeaderDTO;
import com.ideality.coreflow.project.query.dto.ParticipantDepartmentDTO;
import com.ideality.coreflow.project.query.dto.ParticipantUserDTO;
import com.ideality.coreflow.project.query.dto.ResponseParticipantDTO;
import com.ideality.coreflow.project.query.dto.UserInfoDTO;
import com.ideality.coreflow.template.query.dto.DeptDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.ideality.coreflow.project.query.dto.ProjectParticipantDTO;

@Mapper
public interface ParticipantMapper {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);

    List<Long> selectParticipantsList(Long detailParticipantId);

    boolean isParticipantUser(Long userId, Long projectId);

    List<Long> findNextTaskUsersByTaskId(Long taskId);

    List<ProjectParticipantDTO> selectProjectParticipantList(Long projectId);

    boolean isTeamLeader(Long projectId, String deptName);

    boolean isAboveTeamLeader(Long projectId, Long userId);

    boolean isAlreadyParticipant(Long projectId, Long userId, String deptName);

    List<Long> selectParticipantUserId(Long projectId);

    List<ParticipantDepartmentDTO> selectParticipantCountByDept(Long projectId);

    List<DepartmentLeaderDTO> selectTeamLeaderByDepartment(Long projectId);

    List<ResponseParticipantDTO> selectParticipantByDeptName(Long projectId, String deptName);

    List<ParticipantUserDTO> selectAllUserByProjectIds(@Param("projectIds") List<Long> projectIds);

    List<Long> findParticipantsByTaskId(Long taskId);

    Long findDirectorByProjectId(Long projectId);

    //  프로젝트 id로 해당 프로젝트에 참여하는 모든 인원들 정보 가져옴(유저아이디, 유저이름, 부서이름, 직책, 프로필사진)
    List<UserInfoDTO> selectAllUserByProjectId(Long projectId);

    List<ParticipantUserDTO> selectParticipantByTaskId(Long taskId);

    List<DeptDTO> selectLeaderDept(Long projectId);
}
