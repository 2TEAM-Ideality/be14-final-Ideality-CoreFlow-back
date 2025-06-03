package com.ideality.coreflow.holiday.query.service;

public interface DeptQueryService {

    String findNameById(Long id);

    String findDeptCodeByName(String deptName);
}
