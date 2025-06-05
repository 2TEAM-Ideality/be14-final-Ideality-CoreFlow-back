package com.ideality.coreflow.calendar.command.application.service;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.calendar.command.application.dto.RequestScheduleDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarCommandFacadeService {

	private final CalendarCommandService calendarCommandService;
	private final RepeatRuleCommandService repeatRuleCommandService;

	// 개인 일정 생성
	public void createPersonalSchedule(RequestScheduleDTO requestDTO) {

		// 일정 테이블에 개인 일정 생성
		log.info("Create personal schedule");
		Long newScheduleId = calendarCommandService.createPersonalSchedule(requestDTO);

		if(requestDTO.getIsRepeat()){
			log.info("반복 규칙이 있는 개인 일정 생성");
			repeatRuleCommandService.createRepeatSchedule(requestDTO, newScheduleId);
		}
	}
}
