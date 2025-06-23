package com.ideality.coreflow.project.command.domain.service;

import com.ideality.coreflow.project.query.dto.WorkProgressDTO;

import java.util.List;

public interface WorkDomainService {

    double calculateProgressRate(List<WorkProgressDTO> works);
}
