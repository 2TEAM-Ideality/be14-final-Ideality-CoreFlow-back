package com.ideality.coreflow.user.query.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class DeptNameAndYearDTO {
    String deptName;
    LocalDate hireDate;
}
