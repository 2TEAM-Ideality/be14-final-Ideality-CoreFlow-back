package com.ideality.coreflow.org.command.application.service;

import com.ideality.coreflow.org.command.application.dto.ResponseJobRank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrgFacadeService {

    private final JobRankService jobRankService;
    private final JobRoleService jobRoleService;

    @Transactional
    public List<ResponseJobRank> searchAllJobRank() {
        return jobRankService.findAllJobRank();
    }
}
