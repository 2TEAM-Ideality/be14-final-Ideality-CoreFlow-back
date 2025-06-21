package com.ideality.coreflow.calendar.query.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ideality.coreflow.calendar.query.dto.ResponseScheduleDTO;
import com.ideality.coreflow.calendar.query.dto.ScheduleDetailDTO;
import com.ideality.coreflow.calendar.query.dto.TodayScheduleDTO;
import com.ideality.coreflow.calendar.query.service.CalendarQueryFacadeService;
import com.ideality.coreflow.common.response.APIResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController("calendarQueryController")
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Slf4j
public class CalendarQueryController {

	private final CalendarQueryFacadeService calendarQueryFacadeService;

	// TODO. 개인 일정 목록 전체 조회
	@GetMapping("/personal")
	public ResponseEntity<APIResponse<List<ResponseScheduleDTO>>> getPersonalScheduleDetail(
			@RequestParam Long userId
	) {
		List<ResponseScheduleDTO> schedules = calendarQueryFacadeService.getAllPersonalSchedule(userId);
		return ResponseEntity.ok(APIResponse.success(schedules, "개인 일정 목록 조회 성공 ✅"));
	}

	// TODO. 개인 일정 목록 한 달 단위 조회
	@GetMapping("/personal/month")
	public ResponseEntity<APIResponse<List<ResponseScheduleDTO>>> getPersonalScheduleByMonth(
			@RequestParam Long userId,
			@RequestParam int year,
			@RequestParam int month
	){
		log.info(year + "년 " + month + "월에 대한 개인 일정 목록 조회");
		List<ResponseScheduleDTO> schedules = calendarQueryFacadeService.getScheduleByMonth(userId, year, month);
		return ResponseEntity.ok(APIResponse.success(schedules, "개인 일정 목록 조회 성공 ✅"));
	}

	// TODO. 개인 일정 상세 조회
	@GetMapping("/personal/{scheduleId}")
	public ResponseEntity<APIResponse<ScheduleDetailDTO>> getAllPersonalSchedule(
			@PathVariable Long scheduleId,
			@RequestParam Long userId
	){
		ScheduleDetailDTO response = calendarQueryFacadeService.getPersonalDetail(userId, scheduleId);
		return ResponseEntity.ok(APIResponse.success(response, "개인 일정 상세 조회 성공 ✅"));
	}

	// 오늘의 일정 목록 조회
	@GetMapping("/today")
	public ResponseEntity<APIResponse<List<TodayScheduleDTO>>> getPersonalScheduleToday(
			@RequestParam Long userId
	){
			List<TodayScheduleDTO> response = calendarQueryFacadeService.getTodayPersonal(userId);
		return ResponseEntity.ok(APIResponse.success(response, "오늘의 개인 일정 목록 조회 성공 ✅"));
	}

}