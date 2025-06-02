package com.ideality.coreflow.calendar.query.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.calendar.query.dto.ResponsePersonalDTO;
import com.ideality.coreflow.calendar.query.service.CalendarFacadeService;
import com.ideality.coreflow.calendar.query.service.CalendarService;
import com.ideality.coreflow.common.response.APIResponse;

import lombok.RequiredArgsConstructor;

@RestController("calendarQueryController")
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarQueryController {

	private final CalendarFacadeService calendarFacadeService;
	private final CalendarService calendarService;
	// TODO. 내가 속한 부서의 프로젝트 일정 가져오기 (세부일정)


	// TODO. 개인 일정 조회
	@GetMapping("/personal")
	public ResponseEntity<APIResponse<ResponsePersonalDTO>> getPersonalSchedule() {
		// TODO. 로그인한 사용자의 아이디 가져오기
		Long memberId = 1L;
		ResponsePersonalDTO personalSchedules = calendarService.getAllPersonalSchedule(memberId);

		return ResponseEntity.ok(APIResponse.success(personalSchedules, "개인 일정 조회 성공 ✅"));
	}



}
