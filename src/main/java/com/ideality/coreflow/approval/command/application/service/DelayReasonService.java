package com.ideality.coreflow.approval.command.application.service;

import com.ideality.coreflow.approval.command.domain.aggregate.DelayReason;

import java.util.List;

public interface DelayReasonService {
    List<DelayReason> findAllDelayReason();
}
