package com.ideality.coreflow.user.query.service;

import java.util.List;

public interface UserQueryService {
    List<Long> selectByDeptName(String deptName);
}
