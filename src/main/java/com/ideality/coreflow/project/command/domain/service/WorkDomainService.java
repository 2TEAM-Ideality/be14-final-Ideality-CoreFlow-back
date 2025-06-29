package com.ideality.coreflow.project.command.domain.service;

import com.ideality.coreflow.project.command.application.dto.DateInfoDTO;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.query.dto.WorkProgressDTO;

import java.time.LocalDate;
import java.util.List;

public interface WorkDomainService {

    double calculateProgressRate(List<WorkProgressDTO> works);

    double calculatePassedRate(DateInfoDTO dateInfo, TargetType type);

    Integer calculateWorkingDutarion(LocalDate startReal, LocalDate endReal);

    String updateTaskWarning(Long taskId);
}
