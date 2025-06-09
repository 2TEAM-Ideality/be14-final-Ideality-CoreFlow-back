package com.ideality.coreflow.calendar.query.dto;

import java.time.LocalDateTime;

import com.ideality.coreflow.calendar.command.application.dto.FrequencyInfo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseScheduleDTO {
	private Long id;
	private String name;
	private String content;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Boolean isRepeat;
	private Long originalScheduleId;
}
