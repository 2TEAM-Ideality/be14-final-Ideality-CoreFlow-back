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
public class ResponsePersonalDTO {
	private Long id;
	private String scheduleName;
	private String content;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Boolean isRepeat;
}
