package com.ideality.coreflow.project.query.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ProjectDateDTO {
    LocalDate projectStartBase;
    LocalDate projectEndBase;
}
