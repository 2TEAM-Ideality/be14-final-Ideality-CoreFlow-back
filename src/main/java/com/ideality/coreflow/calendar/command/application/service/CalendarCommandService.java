package com.ideality.coreflow.calendar.command.application.service;

import com.ideality.coreflow.calendar.command.application.dto.UpdateScheduleDTO;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import org.springframework.stereotype.Service;

import com.ideality.coreflow.calendar.command.domain.aggregate.EventType;
import com.ideality.coreflow.calendar.command.domain.aggregate.Schedule;
import com.ideality.coreflow.calendar.command.domain.repository.ScheduleRepository;
import com.ideality.coreflow.calendar.command.application.dto.CreateScheduleDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarCommandService {

	private final ScheduleRepository scheduleRepository;

	// 개인 일정 생성
	@Transactional
	public Long createPersonalSchedule(CreateScheduleDTO request) {
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

	@Transactional
	public Long updatePersonalSchedule(UpdateScheduleDTO requestDTO) {
		Schedule originSchedule = scheduleRepository.findById(requestDTO.getScheduleId())
				.orElseThrow(() -> new BaseException(ErrorCode.SCHEDULE_NOT_FOUND));

		originSchedule.updateSchedule(requestDTO);

		return originSchedule.getId();
	}

	@Transactional
	public void deletePersonalSchedule(Long scheduleId) {
		Schedule originSchedule = scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new BaseException(ErrorCode.SCHEDULE_NOT_FOUND));
		originSchedule.deleteSchedule();

	}
}
