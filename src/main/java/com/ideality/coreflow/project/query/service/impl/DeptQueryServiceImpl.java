package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.project.query.dto.DepartmentDTO;
import com.ideality.coreflow.project.query.mapper.DeptMapper;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.ideality.coreflow.common.exception.ErrorCode.DEPARTMENT_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeptQueryServiceImpl implements DeptQueryService {

    private final DeptMapper deptMapper;

    @Override
    public String findNameById(Long id) {
        return deptMapper.findNameById(id)
                .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
    }

    @Override
    public String findDeptCodeByName(String deptName) {
        return deptMapper.findDeptCodeByName(deptName)
                .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
    }

    @Override
    public List<DepartmentDTO> findAllDeptNames() {
        // MyBatis에서 부서명과 부서 ID를 조회한 후, 반환
        List<DepartmentDTO> deptList = deptMapper.findAllDeptNames();

        // 부서 목록이 비어있다면 예외 처리
        if (deptList.isEmpty()) {
            throw new BaseException(DEPARTMENT_NOT_FOUND);
        }

        // 부서 목록 반환
        return deptList;
    }


    @Override
    public Long findDeptIdByName(String deptName) {
        return deptMapper.findDeptIdByName(deptName)
            .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
    }

}

