package com.ideality.coreflow.user.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class DeptNameAndMonthDTO {
    String deptName;
    String yearMonth;
}
