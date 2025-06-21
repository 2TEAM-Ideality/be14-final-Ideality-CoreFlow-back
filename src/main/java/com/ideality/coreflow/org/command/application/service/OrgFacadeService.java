package com.ideality.coreflow.org.command.application.service;

import com.ideality.coreflow.org.command.application.dto.ResponseAllOrgInfo;
import com.ideality.coreflow.org.command.application.dto.ResponseDept;
import com.ideality.coreflow.org.command.application.dto.ResponseJobRank;
import com.ideality.coreflow.org.command.application.dto.ResponseJobRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrgFacadeService {

    private final JobRankService jobRankService;
    private final JobRoleService jobRoleService;
    private final DeptService deptService;
}
