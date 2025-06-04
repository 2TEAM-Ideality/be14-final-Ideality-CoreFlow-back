package com.ideality.coreflow.calendar.command.application.service;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.calendar.query.dto.RequestScheduleDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarCommandFacadeService {

	private final CalendarCommandService calendarCommandService;

	public void createPersonalSchedule(RequestScheduleDTO request) {

	}
}
