package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
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


    @Override
    @Transactional
    public void updateWorkDept(Long detailId, Long newDeptId) {
        // 기존 부서 정보 조회
        WorkDept existingWorkDept = workDeptRepository.findByWorkId(detailId)
                .orElseThrow(() -> new BaseException(ErrorCode.RESOURCE_NOT_FOUND));

        // 부서 수정
        existingWorkDept.setDeptId(newDeptId);
        workDeptRepository.save(existingWorkDept);
        log.info("부서 수정 완료, detailId: {}, newDeptId: {}", detailId, newDeptId);
    }




}
