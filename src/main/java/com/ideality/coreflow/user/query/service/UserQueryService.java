package com.ideality.coreflow.user.query.service;

import com.ideality.coreflow.user.query.dto.ParticipantUserDTO;

import java.util.List;

public interface UserQueryService {
    List<ParticipantUserDTO> selectByDeptName(String deptName);
}
