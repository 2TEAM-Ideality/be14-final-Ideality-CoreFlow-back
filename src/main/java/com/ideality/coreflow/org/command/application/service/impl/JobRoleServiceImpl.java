package com.ideality.coreflow.org.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.org.command.application.dto.ResponseJobRole;
import com.ideality.coreflow.org.command.application.service.JobRoleService;
import com.ideality.coreflow.org.command.domain.aggregate.JobRole;
import com.ideality.coreflow.org.command.domain.repository.JobRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobRoleServiceImpl implements JobRoleService {

    private final JobRoleRepository jobRoleRepository;

    @Override
    public List<ResponseJobRole> findAllJobRole() {
        List<JobRole> result = jobRoleRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return result.stream()
                .map(jobRole -> new ResponseJobRole(jobRole.getId(), jobRole.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public void registJobRole(String name) {
        JobRole jobRole = JobRole.builder().name(name).build();
        jobRoleRepository.save(jobRole);
    }

    @Override
    public void updateJobRole(String prevJobRoleName, String newJobRoleName) {
        JobRole jobRole = jobRoleRepository.findByName(prevJobRoleName).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));
        jobRole.updateNameFrom(newJobRoleName);
        jobRoleRepository.save(jobRole);
    }

    @Override
    public void deleteJobRole(long id) {
        jobRoleRepository.deleteById(id);
    }
}
