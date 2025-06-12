package com.ideality.coreflow.calendar.command.application.service;

import com.ideality.coreflow.calendar.command.application.dto.FrequencyInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ideality.coreflow.calendar.command.domain.aggregate.RepeatRule;
import com.ideality.coreflow.calendar.command.domain.repository.RepeatRuleRepository;
import com.ideality.coreflow.calendar.command.application.dto.CreateScheduleDTO;
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
	public void createRepeatSchedule(CreateScheduleDTO requestDTO, Long scheduleId) {
		log.info("반복 규칙 생성");
		// 일단 반복 규칙 일정이 맞는 지 체크
		if(!requestDTO.getIsRepeat()){
			throw new BaseException(ErrorCode.SCHEDULE_NOT_REPEATABLE);
		}
		// 반복 규칙 테이블에 저장
		RepeatRule repeatRule = RepeatRule.builder()
				.frequency(requestDTO.getFrequencyInfo().getFrequencyType())
				.scheduleId(scheduleId)
				.repeatInterval(requestDTO.getFrequencyInfo().getRepeatInterval())
				.endDate(requestDTO.getFrequencyInfo().getEndDate())
				.byDay(requestDTO.getFrequencyInfo().getByDay())
				.byMonthDay(requestDTO.getFrequencyInfo().getByMonthDay())
				.bySetPos(requestDTO.getFrequencyInfo().getBySetPos())
				.build();

		repeatRuleRepository.save(repeatRule);
	}

	@Transactional
	public void updateRepeatRule(Long updatedScheduleId, FrequencyInfo requestDTO) {
		log.info("반복 규칙 수정");

		RepeatRule originRepeatRule = repeatRuleRepository.findByScheduleId(updatedScheduleId);
		if(originRepeatRule == null){
			throw new BaseException(ErrorCode.REPEATRULE_NOT_FOUND);
		}

		originRepeatRule.updateRepeatRule(requestDTO);
	}

	@Transactional
	public void deleteRepeatRule(Long scheduleId) {
		log.info("반복 규칙 삭제");
		RepeatRule originRepeatRule = repeatRuleRepository.findByScheduleId(scheduleId);
		if(originRepeatRule == null){
			throw new BaseException(ErrorCode.REPEATRULE_NOT_FOUND);
		}
		repeatRuleRepository.delete(originRepeatRule);
	}
}
