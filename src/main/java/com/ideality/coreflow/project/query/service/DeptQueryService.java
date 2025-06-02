package com.ideality.coreflow.project.query.service;

import java.util.List;

public interface DeptQueryService {
    Long findIdByName(String deptName);

    String findDeptCodeByName(String deptName);

    List<String> findAllDeptNames();
}
