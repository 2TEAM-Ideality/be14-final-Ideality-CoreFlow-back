package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.NodeDTO;
import com.ideality.coreflow.project.query.dto.ProjectDetailResponseDTO;
import com.ideality.coreflow.project.query.dto.PipelineResponseDTO;
import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;
import com.ideality.coreflow.project.query.dto.UserInfoDTO;
import com.ideality.coreflow.project.query.dto.WorkDeptDTO;
import com.ideality.coreflow.template.query.dto.DeptDTO;
import com.ideality.coreflow.template.query.dto.EdgeDTO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProjectMapper {
    List<ProjectSummaryDTO> selectParticipatingProjects(Long userId);

    ProjectDetailResponseDTO getProjectDetail(Long projectId);

    List<UserInfoDTO> getProjectLeaders(Long projectId);

    ProjectSummaryDTO selectProjectSummary(Long projectId);

    String findProjectNameByProjectId(Long projectId);

    PipelineResponseDTO findProjectById(@Param("projectId") Long projectId);

    List<NodeDTO> findWorksByProjectId(@Param("projectId") Long projectId);

    List<EdgeDTO> findRelationsByProjectId(@Param("projectId") Long projectId);

    List<DeptDTO> findDeptsByWorkId(@Param("workId") Long workId);

    List<WorkDeptDTO> findDeptsByWorkIds(@Param("workIds") List<Long> workIds);

<<<<<<< HEAD
    String selectProjectNameByProjectId(Long projectId);
=======
>>>>>>> develop
}
