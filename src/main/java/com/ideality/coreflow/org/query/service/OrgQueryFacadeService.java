package com.ideality.coreflow.org.query.service;

import com.ideality.coreflow.org.command.application.dto.ResponseAllOrgInfo;
import com.ideality.coreflow.org.command.application.dto.ResponseDept;
import com.ideality.coreflow.org.command.application.dto.ResponseJobRank;
import com.ideality.coreflow.org.command.application.dto.ResponseJobRole;
import com.ideality.coreflow.org.command.application.service.DeptService;
import com.ideality.coreflow.org.command.application.service.JobRankService;
import com.ideality.coreflow.org.command.application.service.JobRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrgQueryFacadeService {

    private final JobRankService jobRankService;
    private final JobRoleService jobRoleService;
    private final DeptService deptService;

    @Transactional
    public List<ResponseJobRank> searchAllJobRank() {
        return jobRankService.findAllJobRank();
    }

    @Transactional
    public List<ResponseJobRole> searchAllJobRole() {
        return jobRoleService.findAllJobRole();
    }

    @Transactional
    public ResponseAllOrgInfo searchAllOrgInfo() {
        List<ResponseJobRank> jobRankList = jobRankService.findAllJobRank();
        List<ResponseJobRole> jobRoleList = jobRoleService.findAllJobRole();
        List<ResponseDept> deptList = deptService.findAllDept();

        return ResponseAllOrgInfo.builder()
                .jobRankList(jobRankList)
                .jobRoleList(jobRoleList)
                .deptList(deptList)
                .build();
    }

    @Transactional
    public List<ResponseDept> searchAllDept() {
        return deptService.findAllDept();
    }
}
