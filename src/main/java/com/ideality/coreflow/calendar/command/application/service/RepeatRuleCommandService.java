package com.ideality.coreflow.calendar.command.application.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ideality.coreflow.calendar.command.domain.aggregate.Frequency;
import com.ideality.coreflow.calendar.command.domain.aggregate.RepeatRule;
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
	public void createRepeatSchedule(RequestScheduleDTO requestDTO, Long scheduleId) {
		log.info("반복 규칙 생성");
		// 일단 반복 규칙 일정이 맞는 지 체크
		if(!requestDTO.getIsRepeat()){
			throw new BaseException(ErrorCode.SCHEDULE_NOT_REPEATABLE);
		}
		// 반복 규칙 테이블에 저장
		RepeatRule repeatRule = RepeatRule.builder()
			.scheduleId(scheduleId)
			.frequency(requestDTO.getFrequencyInfo().getFrequencyType())
			.repeatInterval(requestDTO.getFrequencyInfo().getRepeatInterval())
			.endDate(LocalDate.from(requestDTO.getEndDate()))
			.byDay(requestDTO.getFrequencyInfo().getByDay())
			.byMonthDay(requestDTO.getFrequencyInfo().getByMonth())
			.bySetPos(requestDTO.getFrequencyInfo().getBySetPos())
			.build();
		repeatRuleRepository.save(repeatRule);

		// 반복규칙 설정
		Frequency frequency = Frequency.valueOf(String.valueOf(requestDTO.getFrequencyInfo().getFrequencyType()));
		log.info("반복 규칙 타입 확인 : " + frequency);

		if(frequency == Frequency.DAILY){
			log.info("데일리 반복 규칙 ");
		}else if(frequency == Frequency.MONTHLY){

		}else{

		}

	}
}
