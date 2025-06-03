package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.project.command.application.service.WorkDeptService;
import com.ideality.coreflow.project.command.domain.aggregate.WorkDept;
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

    @Override
    @Transactional
    public void createWorkDept(Long taskId, Long deptId) {

        WorkDept workDept = WorkDept.builder()
                .workId(taskId)
                .deptId(deptId)
                .build();

        workDeptRepository.save(workDept);
    }
}
