package com.ideality.coreflow.calendar.command.application.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestScheduleDTO {
	private Long createdBy;

	private String name;
	private String content;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	private String eventType;

	private Boolean isRepeat;
	private FrequencyInfo frequencyInfo;
}
