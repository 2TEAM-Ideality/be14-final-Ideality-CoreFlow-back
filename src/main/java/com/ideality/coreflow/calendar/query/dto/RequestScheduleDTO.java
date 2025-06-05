package com.ideality.coreflow.calendar.query.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestScheduleDTO {
	private String name;
	private String description;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

}
