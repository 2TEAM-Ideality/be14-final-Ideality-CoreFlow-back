package com.ideality.coreflow.project.query.service;

public interface DeptQueryService {

    String findNameById(Long id);

    String findDeptCodeByName(String deptName);

    Long findDeptIdByName(String deptName);
}
