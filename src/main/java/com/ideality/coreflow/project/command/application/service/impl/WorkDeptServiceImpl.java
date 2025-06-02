package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.project.command.application.service.WorkDeptService;
import com.ideality.coreflow.project.command.domain.aggregate.Dept;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.aggregate.WorkDept;
import com.ideality.coreflow.project.command.domain.repository.DeptRepository;
import com.ideality.coreflow.project.command.domain.repository.TaskRepository;
import com.ideality.coreflow.project.command.domain.repository.WorkDeptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkDeptServiceImpl implements WorkDeptService {

    private final WorkDeptRepository workDeptRepository;
    private final TaskRepository taskRepository;
    private final DeptRepository deptRepository;

    @Override
    @Transactional
    public void createWorkDept(Long taskId, Long deptId) {

        Work findWork = taskRepository.getReferenceById(taskId);
        Dept findDept = deptRepository.getReferenceById(deptId);
        WorkDept workDept = WorkDept.builder()
                .work(findWork)
                .dept(findDept)
                .build();

        workDeptRepository.save(workDept);
    }
}
