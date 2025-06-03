package com.ideality.coreflow.dept.query.service;

public interface DeptQueryService {

    String findNameById(Long id);

    String findDeptCodeByName(String deptName);
}
