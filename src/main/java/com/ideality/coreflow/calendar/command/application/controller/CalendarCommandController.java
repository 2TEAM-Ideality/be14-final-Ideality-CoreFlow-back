package com.ideality.coreflow.calendar.command.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.calendar.command.application.service.CalendarCommandFacadeService;
import com.ideality.coreflow.calendar.query.dto.RequestScheduleDTO;
import com.ideality.coreflow.calendar.query.dto.RequestUserDTO;
import com.ideality.coreflow.calendar.query.dto.ResponseScheduleDTO;
import com.ideality.coreflow.common.response.APIResponse;

import lombok.RequiredArgsConstructor;

@RestController("calenderCommandController")
@RequiredArgsConstructor
@RequestMapping("/api/calendar")
public class CalendarCommandController {

	private CalendarCommandFacadeService calendarCommandFacadeService;


	// 개인 일정 생성
	@PostMapping("/create")
	public ResponseEntity<APIResponse<ResponseScheduleDTO>> createPersonalSchedule(@RequestBody RequestScheduleDTO request) {

		calendarCommandFacadeService.createPersonalSchedule(request);
		ResponseScheduleDTO response = new ResponseScheduleDTO();
		return ResponseEntity.ok(APIResponse.success(response, "개인 일정이 성공적으로 생성되었습니다. ✅"));
	}

	// 개인 일정 수정

	// 개인 일정 삭제
	
}
