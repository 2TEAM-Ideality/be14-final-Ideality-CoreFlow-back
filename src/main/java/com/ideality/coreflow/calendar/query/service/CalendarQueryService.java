package com.ideality.coreflow.calendar.query.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.calendar.command.application.dto.FrequencyInfo;
import com.ideality.coreflow.calendar.query.dto.ResponseScheduleDTO;
import com.ideality.coreflow.calendar.query.dto.ScheduleDetailDTO;
import com.ideality.coreflow.calendar.query.dto.TodayScheduleDTO;
import com.ideality.coreflow.calendar.query.mapper.CalendarMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarQueryService {

	private final CalendarMapper calendarMapper;

	public ScheduleDetailDTO getPersonalDetail(Long userId, Long scheduleId) {
		Map<String, Object> param = new HashMap<>();
		param.put("memberId", userId);
		param.put("taskId", scheduleId);
		ScheduleDetailDTO detail = calendarMapper.selectScheduleDetail(param);

		if(detail.getIsRepeat()) {
			FrequencyInfo frequencyInfo = calendarMapper.selectRepeatRule(scheduleId);
			detail.setFrequencyInfo(frequencyInfo);
		}
		return detail;
	}

	public List<ResponseScheduleDTO> getAllPersonalSchedule(Long userId) {
		return calendarMapper.selectAllPersonal(userId);
	}

	public List<TodayScheduleDTO> getTodayPersonal(Long userId, LocalDateTime today) {
		Map<String, Object> param = new HashMap<>();
		param.put("userId", userId);
		param.put("today", today);
		return calendarMapper.selectTodayPersonal(param);
	}

	// 년-월에 해당하는 목록 조회
	public List<ResponseScheduleDTO> getScheduleByMonth(Long userId, int year, int month) {

		LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
		LocalDateTime endOfMonth = startOfMonth
				.withDayOfMonth(startOfMonth.toLocalDate().lengthOfMonth())
				.withHour(23).withMinute(59).withSecond(59);

		Map<String, Object> param = new HashMap<>();
		param.put("userId", userId);
		param.put("startOfMonth", Timestamp.valueOf(startOfMonth));
		param.put("endOfMonth", Timestamp.valueOf(endOfMonth));

		return calendarMapper.selectScheduleByMonth(param);
	}

	public List<ScheduleDetailDTO> selectRepeatingSchedulesWithRules(Long userId) {
		Map<String, Object> param = new HashMap<>();
		param.put("userId", userId);

		// 1. 반복 일정만 조회 (is_repeat = true)
		List<ScheduleDetailDTO> repeatSchedules = calendarMapper.selectRepeatingSchedules(param);

		// 2. 각 일정에 대해 반복 규칙 추가
		for (ScheduleDetailDTO schedule : repeatSchedules) {
			FrequencyInfo frequencyInfo = calendarMapper.selectRepeatRule(schedule.getId());
			schedule.setFrequencyInfo(frequencyInfo);

			System.out.println("[반복 일정] " + schedule.getName());
			System.out.println("[반복 규칙] " + frequencyInfo.getFrequencyType() + ", every " + frequencyInfo.getRepeatInterval());
		}

		return repeatSchedules;
	}

}