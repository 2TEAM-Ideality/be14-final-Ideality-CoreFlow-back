package com.ideality.coreflow.calendar.command.application.controller;

import com.ideality.coreflow.calendar.command.application.dto.UpdateScheduleDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ideality.coreflow.calendar.command.application.service.CalendarCommandFacadeService;
import com.ideality.coreflow.calendar.command.application.dto.CreateScheduleDTO;
import com.ideality.coreflow.common.response.APIResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@RestController("calenderCommandController")
@RequiredArgsConstructor
@RequestMapping("/api/calendar")
@Slf4j
public class CalendarCommandController {

	private final CalendarCommandFacadeService calendarCommandFacadeService;


	// 개인 일정 생성
	@PostMapping("/create")
	public ResponseEntity<APIResponse<?>> createPersonalSchedule(@RequestBody CreateScheduleDTO request) {
		log.info("개인 일정 생성 요청");

		calendarCommandFacadeService.createPersonalSchedule(request);
		return ResponseEntity.ok(APIResponse.success(null, "개인 일정이 성공적으로 생성되었습니다. ✅"));
	}

	// 개인 일정 수정
	@PatchMapping("/edit")
	public ResponseEntity<APIResponse<Map<String, Long>>> editPersonalSchedule(@RequestBody UpdateScheduleDTO request) {
		log.info("개인 일정 수정 요청");
		Long updatedScheduleId = calendarCommandFacadeService.updatePersonalSchedule(request);
		Map<String, Long> response = new HashMap<>();
		response.put("updatedSchedule-", updatedScheduleId);
		return ResponseEntity.ok(APIResponse.success(response, "개인 일정이 성공적으로 수정되었습니다. ✅"));
		
	}

	// 개인 일정 삭제
	@DeleteMapping("/delete")
	public ResponseEntity<APIResponse<Map<String, Long>>> deletePersonalSchedule(@RequestParam("scheduleId") Long scheduleId, @RequestParam("userId") Long userId) {
		log.info("개인 일정 삭제 요청");
		calendarCommandFacadeService.deletePersonalSchedule(userId, scheduleId);
		Map<String, Long> response = new HashMap<>();
		response.put("deletedSchedule-", scheduleId);
		return ResponseEntity.ok(APIResponse.success(response, "개인 일정이 성공적으로 삭제되었습니다. ✅"));

	}
	
}
