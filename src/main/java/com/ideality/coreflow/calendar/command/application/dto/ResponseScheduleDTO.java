package com.ideality.coreflow.calendar.command.application.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ResponseScheduleDTO {
	private Long createdBy;

	private String name;
	private String content;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	private String eventType;
}
