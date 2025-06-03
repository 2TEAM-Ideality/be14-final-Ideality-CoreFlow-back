package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.project.command.application.service.DeptService;
import com.ideality.coreflow.project.command.domain.repository.DeptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final DeptRepository deptRepository;
}
