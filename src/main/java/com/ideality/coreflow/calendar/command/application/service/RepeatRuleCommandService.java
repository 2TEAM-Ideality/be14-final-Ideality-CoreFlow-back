package com.ideality.coreflow.calendar.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ideality.coreflow.calendar.command.domain.repository.RepeatRuleRepository;
import com.ideality.coreflow.calendar.command.application.dto.RequestScheduleDTO;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepeatRuleCommandService {

	private final RepeatRuleRepository repeatRuleRepository;

	// 반복 일정 데이터 생성
	@Transactional
	public void createRepeatSchedule(RequestScheduleDTO requestDTO, Long targetScheduleId) {
		log.info("반복 규칙 생성");
		// 일단 반복 규칙 일정이 맞는 지 체크
		if(!requestDTO.getIsRepeat()){
			throw new BaseException(ErrorCode.SCHEDULE_NOT_REPEATABLE);
		}
		// 반복규칙 설정
		// switch (requestDTO.getRepeatType()){
		// }

		// RepeatRule repeatRule = RepeatRule.builder()
		// 	.scheduleId(targetScheduleId)
		// 	.
	}
}
