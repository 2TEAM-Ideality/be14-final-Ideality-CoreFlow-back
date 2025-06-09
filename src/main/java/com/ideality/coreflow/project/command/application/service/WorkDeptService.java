package com.ideality.coreflow.project.command.application.service;

public interface WorkDeptService {
    void createWorkDept(Long taskId, Long deptId);
    void updateWorkDept(Long taskId, Long newDeptId);
}
