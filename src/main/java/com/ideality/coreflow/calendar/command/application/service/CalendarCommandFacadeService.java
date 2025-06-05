package com.ideality.coreflow.calendar.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ideality.coreflow.calendar.command.application.dto.RequestScheduleDTO;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.user.query.service.UserQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarCommandFacadeService {

	private final CalendarCommandService calendarCommandService;
	private final RepeatRuleCommandService repeatRuleCommandService;
	private final UserQueryService userQueryService;

	// 개인 일정 생성
	@Transactional
	public void createPersonalSchedule(RequestScheduleDTO requestDTO) {

		// 일정 테이블에 개인 일정 생성
		log.info("Create personal schedule");
		if(!userQueryService.selectUserById(requestDTO.getCreatedBy())){
			throw new BaseException(ErrorCode.USER_NOT_FOUND);
		}

		Long newScheduleId = calendarCommandService.createPersonalSchedule(requestDTO);

		if(requestDTO.getIsRepeat()){
			log.info("반복 규칙이 있는 개인 일정 생성");
			repeatRuleCommandService.createRepeatSchedule(requestDTO, newScheduleId);
		}
	}
}
