package com.ideality.coreflow.calendar.command.application.service;

import com.ideality.coreflow.calendar.command.application.dto.UpdateScheduleDTO;
import com.ideality.coreflow.calendar.query.service.CalendarQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ideality.coreflow.calendar.command.application.dto.CreateScheduleDTO;
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
	public void createPersonalSchedule(CreateScheduleDTO requestDTO) {

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

	@Transactional
	public Long updatePersonalSchedule(UpdateScheduleDTO requestDTO) {
		log.info("Update personal schedule");
		if(!userQueryService.selectUserById(requestDTO.getCreatedBy())){
			throw new BaseException(ErrorCode.USER_NOT_FOUND);
		}
		Long updatedScheduleId = calendarCommandService.updatePersonalSchedule(requestDTO);
		if(requestDTO.getIsRepeat() && requestDTO.getFrequencyInfo() != null){
			log.info("반복 규칙 수정");
			repeatRuleCommandService.updateRepeatRule(updatedScheduleId, requestDTO.getFrequencyInfo());
		}

	 	return updatedScheduleId;
	}

	@Transactional
	public void deletePersonalSchedule(Long userId, Long scheduleId) {
		log.info("Delete personal schedule");
		if(!userQueryService.selectUserById(userId)){
			throw new BaseException(ErrorCode.USER_NOT_FOUND);
		}

		calendarCommandService.deletePersonalSchedule(scheduleId);
		repeatRuleCommandService.deleteRepeatRule(scheduleId);

	}
}
