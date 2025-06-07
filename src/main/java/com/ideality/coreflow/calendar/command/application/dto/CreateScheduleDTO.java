package com.ideality.coreflow.calendar.command.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateScheduleDTO {
	// 개인 일정 생성
	private Long createdBy;

	private String name;
	private String content;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	private String eventType;

	private Boolean isRepeat;
	private FrequencyInfo frequencyInfo;

	private List<LocalDateTime> reminder;  // 리마인더
}
