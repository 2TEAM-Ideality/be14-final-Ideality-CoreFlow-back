package com.ideality.coreflow.org.query.service;

import com.ideality.coreflow.project.query.dto.DepartmentDTO;
import com.ideality.coreflow.project.query.dto.ParticipantDepartmentDTO;

import java.util.List;

public interface DeptQueryService {

    String findNameById(Long id);

    String findDeptCodeByName(String deptName);

    List<DepartmentDTO> findAllDeptNames();

    Long findDeptIdByName(String deptName);
}
