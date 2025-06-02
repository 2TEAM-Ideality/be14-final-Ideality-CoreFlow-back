package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.project.query.mapper.DeptMapper;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.ideality.coreflow.common.exception.ErrorCode.DEPARTMENT_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeptQueryServiceImpl implements DeptQueryService {

    private final DeptMapper deptMapper;

    @Override
    public Long findIdByName(String deptName) {
        return deptMapper.findIdByDeptName(deptName)
                .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
    }

    @Override
    public String findDeptCodeByName(String deptName) {
        return deptMapper.findDeptCodeByName(deptName)
                .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
    }
}
