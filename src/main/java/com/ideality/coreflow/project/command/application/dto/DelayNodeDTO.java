package com.ideality.coreflow.project.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DelayNodeDTO {
    Long taskId;
    Integer delayDays;
}
