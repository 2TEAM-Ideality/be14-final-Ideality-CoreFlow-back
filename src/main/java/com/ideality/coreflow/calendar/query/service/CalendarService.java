package com.ideality.coreflow.calendar.query.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.calendar.query.dto.ResponseScheduleDTO;
import com.ideality.coreflow.calendar.query.dto.TodayScheduleDTO;
import com.ideality.coreflow.calendar.query.mapper.CalendarMapper;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarService {

	private final CalendarMapper calendarMapper;

	public ResponseScheduleDTO getPersonalDetail(Long userId, Long taskId) {
		Map<String, Object> param = new HashMap<>();
		param.put("memberId", userId);
		param.put("taskId", taskId);

		return calendarMapper.selectPersonalDetail(param);
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
}
