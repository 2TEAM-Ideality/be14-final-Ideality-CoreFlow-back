package com.ideality.coreflow.approval.command.application.service.impl;

import com.ideality.coreflow.approval.command.application.service.DelayReasonService;
import com.ideality.coreflow.approval.command.domain.aggregate.DelayReason;
import com.ideality.coreflow.approval.command.domain.repository.DelayReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DelayReasonServiceImpl implements DelayReasonService {

    private final DelayReasonRepository delayReasonRepository;

    @Override
    public List<DelayReason> findAllDelayReason() {
        return delayReasonRepository.findAll();
    }
}
