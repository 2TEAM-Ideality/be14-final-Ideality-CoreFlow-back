package com.ideality.coreflow.project.query.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HolidayQueryDto {
    private LocalDate date;
    private String name;
    private String type;
}
