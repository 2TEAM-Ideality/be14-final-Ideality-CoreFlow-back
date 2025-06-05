package com.ideality.coreflow.calendar.query.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodayScheduleDTO {
	private Long id;
	private String scheduleName;
	private String content;

	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Long leftDateTime;
	private Boolean  isToday;

	private Boolean isRepeat;
}
