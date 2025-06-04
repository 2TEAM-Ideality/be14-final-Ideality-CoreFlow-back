package com.ideality.coreflow.org.command.application.service;

import com.ideality.coreflow.org.command.application.dto.ResponseJobRole;

import java.util.List;

public interface JobRoleService {

    List<ResponseJobRole> findAllJobRole();

    void registJobRole(String name);

    void updateJobRole(String prevJobRoleName, String newJobRoleName);

    void deleteJobRole(long id);
}
