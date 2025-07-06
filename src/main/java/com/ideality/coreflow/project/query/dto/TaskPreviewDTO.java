package com.ideality.coreflow.project.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class TaskPreviewDTO {
    Long id;
    String name;
    LocalDate startExpect;
    LocalDate endExpect;
}
