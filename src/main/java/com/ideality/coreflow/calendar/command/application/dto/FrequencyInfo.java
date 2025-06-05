package com.ideality.coreflow.calendar.command.application.dto;

import java.util.Date;

import com.ideality.coreflow.calendar.command.domain.aggregate.Frequency;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FrequencyInfo {
	Long id;
	Long scheduleId;

	Frequency frequencyType;
	Integer repeatInterval;

	Date endDate;
	String byDay;
	Integer byMonth;
	Integer byYear;
	Integer bySetPos;
}
