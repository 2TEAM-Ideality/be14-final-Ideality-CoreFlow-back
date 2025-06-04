package com.ideality.coreflow.calendar.query.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.calendar.query.dto.ResponsePersonalDTO;
import com.ideality.coreflow.calendar.query.service.CalendarService;
import com.ideality.coreflow.common.response.APIResponse;

import lombok.RequiredArgsConstructor;

@RestController("calendarQueryController")
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarQueryController {

	private final CalendarService calendarService;
	// TODO. 내가 속한 부서의 프로젝트 일정 가져오기 (세부일정)


	// TODO. 개인 일정 목록 조회
	@GetMapping("/personal")
	public ResponseEntity<APIResponse<List<ResponsePersonalDTO>>> getPersonalSchedule() {
		// TODO. 로그인한 사용자의 아이디 가져오기
		Long memberId = 1L;
		List<ResponsePersonalDTO> personalSchedules = calendarService.getAllPersonalSchedule(memberId);

		return ResponseEntity.ok(APIResponse.success(personalSchedules, "개인 일정 목록 조회 성공 ✅"));
	}

	// TODO. 개인 일정 상세 조회
	@GetMapping("/personal/{taskId}")
	public ResponseEntity<APIResponse<ResponsePersonalDTO>> getPersonalSchedule(@PathVariable Long taskId) {
		// TODO. 로그인한 사용자의 아이디 가져오기
		Long memberId = 1L;
		ResponsePersonalDTO response = calendarService.getPersonalDetail(memberId, taskId);

		return ResponseEntity.ok(APIResponse.success(response, "개인 일정 상세 조회 성공 ✅"));
	}



}
