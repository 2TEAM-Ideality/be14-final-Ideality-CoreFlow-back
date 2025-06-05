package com.ideality.coreflow.calendar.command.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.calendar.command.application.service.CalendarCommandFacadeService;
import com.ideality.coreflow.calendar.command.application.dto.RequestScheduleDTO;
import com.ideality.coreflow.calendar.query.dto.ResponseScheduleDTO;
import com.ideality.coreflow.common.response.APIResponse;

import ch.qos.logback.classic.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController("calenderCommandController")
@RequiredArgsConstructor
@RequestMapping("/api/calendar")
@Slf4j
public class CalendarCommandController {

	private final CalendarCommandFacadeService calendarCommandFacadeService;


	// 개인 일정 생성
	@PostMapping("/create")
	public ResponseEntity<APIResponse<?>> createPersonalSchedule(@RequestBody RequestScheduleDTO request) {
		log.info("개인 일정 생성 요청");
		calendarCommandFacadeService.createPersonalSchedule(request);
		return ResponseEntity.ok(APIResponse.success(null, "개인 일정이 성공적으로 생성되었습니다. ✅"));
	}

	// 개인 일정 수정

	// 개인 일정 삭제
	
}
