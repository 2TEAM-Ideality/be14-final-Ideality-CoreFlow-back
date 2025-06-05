package com.ideality.coreflow.calendar.command.application.service;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.calendar.command.domain.aggregate.EventType;
import com.ideality.coreflow.calendar.command.domain.aggregate.Schedule;
import com.ideality.coreflow.calendar.command.domain.repository.ScheduleRepository;
import com.ideality.coreflow.calendar.command.application.dto.RequestScheduleDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarCommandService {

	private final ScheduleRepository scheduleRepository;

	// 개인 일정 생성
	@Transactional
	public Long createPersonalSchedule(RequestScheduleDTO request) {
		Schedule newSchedule = Schedule.builder()
			.userId(request.getCreatedBy())
			.name(request.getName())
			.content(request.getContent())
			.startAt(request.getStartDate())
			.endAt(request.getEndDate())
			.isRepeat(request.getIsRepeat())
			.eventType(EventType.valueOf(request.getEventType()))
			.build();
		newSchedule = scheduleRepository.save(newSchedule);
		return newSchedule.getId();
	}
}
