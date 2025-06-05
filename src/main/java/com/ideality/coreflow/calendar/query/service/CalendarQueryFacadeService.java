package com.ideality.coreflow.calendar.query.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.calendar.query.dto.ResponseDeptScheduleDTO;
import com.ideality.coreflow.calendar.query.dto.ResponseScheduleDTO;
import com.ideality.coreflow.calendar.query.dto.TodayScheduleDTO;
import com.ideality.coreflow.calendar.query.mapper.CalendarMapper;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.user.query.service.UserQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarQueryFacadeService {

	private final CalendarService calendarService;
	private final UserQueryService userQueryService;

	// 개인 일정 상세 정보 조회
	public ResponseScheduleDTO getPersonalDetail(Long userId, Long taskId) {
		if(!userQueryService.selectUserById(userId)){
			throw new BaseException(ErrorCode.USER_NOT_FOUND);
		}// getPersonalDetail
		ResponseScheduleDTO result = calendarService.getPersonalDetail(userId, taskId);
		if (result == null) {
			throw new BaseException(ErrorCode.SCHEDULE_NOT_FOUND);
		}
		return result;
	}

	// 개인 일정 목록 조회
	public List<ResponseScheduleDTO> getAllPersonalSchedule(Long userId) {
		// 유저 확인
		if(!userQueryService.selectUserById(userId)){
			throw new BaseException(ErrorCode.USER_NOT_FOUND);
		}
		return calendarService.getAllPersonalSchedule(userId);
	}

	// 오늘의 개인 일정 목록 조회
	public List<TodayScheduleDTO> getTodayPersonal(Long userId) {
		if(!userQueryService.selectUserById(userId)){
			throw new BaseException(ErrorCode.USER_NOT_FOUND);
		}

		// 오늘의 날짜. 및 시간
		LocalDateTime now = LocalDateTime.now();    // 2019-11-12T16:34:30.388

		// 오늘 일정 목록 가져오기
		List<TodayScheduleDTO> scheduleList = calendarService.getTodayPersonal(userId, now);

		// leftDateTime, isToday 계산해서 DTO에 추가
		return scheduleList.stream().peek(schedule -> {
			schedule.setIsToday(
				!now.toLocalDate().isBefore(schedule.getStartAt().toLocalDate()) &&
					!now.toLocalDate().isAfter(schedule.getEndAt().toLocalDate())
			);
			schedule.setLeftDateTime(Duration.between(now, schedule.getStartAt()).toMinutes());
		}).toList();
	}
}
