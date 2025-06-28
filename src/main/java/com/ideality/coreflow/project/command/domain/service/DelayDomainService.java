package com.ideality.coreflow.project.command.domain.service;

import com.ideality.coreflow.approval.command.application.dto.DelayInfoDTO;

public interface DelayDomainService {
    DelayInfoDTO delayAndPropagateLogic(Long taskId, int delayDays, boolean isSimulate);
}
