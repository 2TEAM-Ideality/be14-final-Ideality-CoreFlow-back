package com.ideality.coreflow.project.query.service;

import java.util.List;

public interface DeptQueryService {

    String findNameById(Long id);

    String findDeptCodeByName(String deptName);

    List<String> findAllDeptNames();

    Long findDeptIdByName(String deptName);
}
