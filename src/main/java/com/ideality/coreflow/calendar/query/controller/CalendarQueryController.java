package com.ideality.coreflow.calendar.query.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.calendar.query.dto.RequestUserDTO;
import com.ideality.coreflow.calendar.query.dto.RequestYearMonthDTO;
import com.ideality.coreflow.calendar.query.dto.ResponseDeptScheduleDTO;
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
		@RequestBody RequestUserDTO requestDTO
	) {
		// TODO. 로그인한 사용자의 아이디 가져오기
		List<ResponseScheduleDTO> schedules = calendarQueryFacadeService.getAllPersonalSchedule(requestDTO.getUserId());
		return ResponseEntity.ok(APIResponse.success(schedules, "개인 일정 목록 조회 성공 ✅"));
	}

	// TODO. 개인 일정 목록 한 달 단위 조회
	@GetMapping("/personal/month")
	public ResponseEntity<APIResponse<List<ResponseScheduleDTO>>> getPersonalScheduleByMonth(
		@RequestBody RequestYearMonthDTO requestDTO
	){
		log.info(requestDTO.getYear() + "년" + requestDTO.getMonth() +"월에 대한 개인 일정 목록 조회");
		List<ResponseScheduleDTO> schedules = calendarQueryFacadeService.getScheduleByMonth(requestDTO.getUserId(), requestDTO.getYear(), requestDTO.getMonth());

		return ResponseEntity.ok(APIResponse.success(schedules, "개인 일정 목록 조회 성공 ✅"));
	}

	// TODO. 개인 일정 상세 조회
	@GetMapping("/personal/{scheduleId}")
	public ResponseEntity<APIResponse<ScheduleDetailDTO>> getAllPersonalSchedule(
		@RequestBody RequestUserDTO requestDTO,
		@PathVariable Long scheduleId
	){
		ScheduleDetailDTO response = calendarQueryFacadeService.getPersonalDetail(requestDTO.getUserId(), scheduleId);

		return ResponseEntity.ok(APIResponse.success(response, "개인 일정 상세 조회 성공 ✅"));
	}

	// TODO. 오늘의 일정 목록 조회
	@GetMapping("/today")
	public ResponseEntity<APIResponse<List<TodayScheduleDTO>>> getPersonalScheduleToday(
		@RequestBody RequestUserDTO requestDTO
	){
		List<TodayScheduleDTO> response = calendarQueryFacadeService.getTodayPersonal(requestDTO.getUserId());

		return ResponseEntity.ok(APIResponse.success(response, "오늘의 개인 일정 목록 조회 성공 ✅"));
	}

}
