package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.query.dto.ResponseDeptIdDTO;

public interface DeptQueryService {
    Long findIdByName(String deptName);
}
