package com.ideality.coreflow.holiday.query.service;

public interface DeptQueryService {
    Long findIdByName(String deptName);

    String findDeptCodeByName(String deptName);
}
