package com.ideality.coreflow.approval.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DelayTaskNameDTO {
    String name;
    Integer delayDays;
}
