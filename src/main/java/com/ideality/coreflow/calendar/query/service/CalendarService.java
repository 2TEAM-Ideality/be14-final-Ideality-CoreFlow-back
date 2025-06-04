package com.ideality.coreflow.calendar.query.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.calendar.query.dto.ResponsePersonalDTO;
import com.ideality.coreflow.calendar.query.mapper.CalendarMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarService {

	private final CalendarMapper calendarMapper;

	public List<ResponsePersonalDTO> getAllPersonalSchedule(Long memberId) {
		return calendarMapper.selectAllPersonal(memberId);
	}

	public ResponsePersonalDTO getPersonalDetail(Long memberId, Long taskId) {
		Map<String, Object> param = new HashMap<>();
		param.put("memberId", memberId);
		param.put("taskId", taskId);

		return calendarMapper.selectPersonalDetail(param);
	}
}
