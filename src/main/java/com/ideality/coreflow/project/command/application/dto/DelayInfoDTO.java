package com.ideality.coreflow.project.command.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Builder
public class DelayInfoDTO {
    LocalDate originProjectEndExpect;
    LocalDate newProjectEndExpect;
    int delayDaysByTask;
    int taskCountByDelay;
    Map<Long, Integer> delayDaysByTaskId;
}
