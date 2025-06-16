package com.ideality.coreflow.approval.query.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseDelayReason {
    long id;
    String reason;
}
