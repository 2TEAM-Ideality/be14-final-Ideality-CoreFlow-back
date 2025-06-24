package com.ideality.coreflow.project.query.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class WorkProgressDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double progressRate;
}
