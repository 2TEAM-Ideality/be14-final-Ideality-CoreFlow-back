package com.ideality.coreflow.org.command.application.service.impl;

import com.ideality.coreflow.org.command.application.dto.ResponseDept;
import com.ideality.coreflow.org.command.application.dto.ResponseJobRole;
import com.ideality.coreflow.org.command.application.service.DeptService;
import com.ideality.coreflow.org.command.domain.aggregate.Dept;
import com.ideality.coreflow.org.command.domain.aggregate.JobRole;
import com.ideality.coreflow.org.command.domain.repository.DeptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final DeptRepository deptRepository;

    @Override
    public List<ResponseDept> findAllDept() {
        List<Dept> result = deptRepository.findAllByIsDeletedFalse();
        return result.stream()
                .map(dept -> new ResponseDept(dept.getId(), dept.getName(), dept.getDeptCode(), dept.getParentDeptId()))
                .collect(Collectors.toList());
    }
}
