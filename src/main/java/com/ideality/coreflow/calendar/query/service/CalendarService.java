package com.ideality.coreflow.calendar.query.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.calendar.query.dto.ResponseScheduleDTO;
import com.ideality.coreflow.calendar.query.mapper.CalendarMapper;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarService {

	private final CalendarMapper calendarMapper;

	public List<ResponseScheduleDTO> getAllPersonalSchedule(Long userId) {
		return calendarMapper.selectAllPersonal(userId)
			.orElseThrow(()-> new BaseException(ErrorCode.FORBIDDEN));
	}

	public ResponseScheduleDTO getPersonalDetail(Long userId, Long taskId) {
		Map<String, Object> param = new HashMap<>();
		param.put("memberId", userId);
		param.put("taskId", taskId);

		return calendarMapper.selectPersonalDetail(param)
			.orElseThrow(()-> new BaseException(ErrorCode.FORBIDDEN));
	}

	public List<ResponseScheduleDTO> getAllDeptSchedule(Long deptId) {
		return calendarMapper.seelctAllDeptSchedule(deptId)
			.orElseThrow(()-> new BaseException(ErrorCode.FORBIDDEN));
	}
}
