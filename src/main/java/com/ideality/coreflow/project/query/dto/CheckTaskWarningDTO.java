package com.ideality.coreflow.project.query.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class CheckTaskWarningDTO {
    LocalDate taskEndExpect;
    LocalDate subTaskEndExpect;
}
