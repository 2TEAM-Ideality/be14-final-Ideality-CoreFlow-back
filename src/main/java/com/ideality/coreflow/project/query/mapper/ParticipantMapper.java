package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.DepartmentLeaderDTO;
import com.ideality.coreflow.project.query.dto.ParticipantDepartmentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ParticipantMapper {
    Long selectDirectorByProjectId(Long projectId);

    boolean isProjectDirector(Long projectId, Long userId);

    List<Long> selectParticipantsList(Long detailParticipantId);

    boolean isParticipantUser(Long userId, Long projectId);

    List<ParticipantDepartmentDTO> selectParticipantCountByDept(Long projectId);

    List<DepartmentLeaderDTO> selectTeamLeaderByDepartment(Long projectId);
}
