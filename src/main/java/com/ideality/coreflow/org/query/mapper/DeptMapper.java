package com.ideality.coreflow.org.query.mapper;

import com.ideality.coreflow.project.query.dto.DepartmentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DeptMapper {
    Optional<String> findNameById(Long id);

    Optional<String> findDeptCodeByName(String deptName);

    List<DepartmentDTO> findAllDeptNames();

    Optional<Long> findDeptIdByName(String deptName);

    List<DepartmentDTO> findDeptListByTaskId(Long workId);
}
