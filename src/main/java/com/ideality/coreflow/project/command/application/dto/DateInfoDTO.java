package com.ideality.coreflow.project.command.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DateInfoDTO {
    LocalDate startBase;
    LocalDate endBase;
    LocalDate startReal;
    LocalDate endReal;
    LocalDate startExpect;
    LocalDate endExpect;
}
