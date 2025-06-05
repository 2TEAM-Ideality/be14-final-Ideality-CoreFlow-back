package com.ideality.coreflow.calendar.query.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RequestYearMonthDTO {
	Long userId;
	Integer year;
	Integer month;
}
