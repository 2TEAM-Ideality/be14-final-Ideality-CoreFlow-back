package com.ideality.coreflow.calendar.query.service;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.calendar.query.dto.ResponsePersonalDTO;
import com.ideality.coreflow.calendar.query.mapper.CalendarMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarService {

	private final CalendarMapper calendarMapper;

	public ResponsePersonalDTO getAllPersonalSchedule(Long memberId) {
		return calendarMapper.selectAllPersonal(memberId);
	}
}
