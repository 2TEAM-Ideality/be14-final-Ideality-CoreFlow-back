package com.ideality.coreflow.calendar.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.calendar.query.dto.ResponseScheduleDTO;
import com.ideality.coreflow.calendar.query.mapper.CalendarMapper;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.user.query.service.UserQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarQueryFacadeService {

	private final CalendarMapper calendarMapper;
	private final CalendarService calendarService;
	private final UserQueryService userQueryService;
	private final DeptQueryService deptQueryService;

	// 부서 일정 목록 조회
	// public List<ResponseScheduleDTO> getDeptScheduleList(Long userId) {
	//
	// 	// TODO. 로그인한 유저 정보 가져오기
	//
	// 	String deptName = userQueryService.getDeptNameByUserId(userId);
	// 	Long deptId = deptQueryService.findDeptIdByName(deptName);
	//
	// 	return
	// }

	// 개인 일정 상세 정보 조회
	public ResponseScheduleDTO getPersonalDetail(Long userId, Long taskId) {
		return calendarService.getPersonalDetail(userId, taskId);
	}

	// 개인 일정 목록 조회
	public List<ResponseScheduleDTO> getAllPersonalSchedule(Long userId) {
		return calendarService.getAllPersonalSchedule(userId);
	}

}
