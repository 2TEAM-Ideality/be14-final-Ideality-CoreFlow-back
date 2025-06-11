package com.ideality.coreflow.org.command.application.service;

import com.ideality.coreflow.org.command.application.dto.ResponseDept;

import java.util.List;

public interface DeptService {
    List<ResponseDept> findAllDept();
}
