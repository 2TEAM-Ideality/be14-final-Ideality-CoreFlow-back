package com.ideality.coreflow.calendar.query.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.calendar.query.dto.RequestUserDTO;
import com.ideality.coreflow.calendar.query.dto.ResponseScheduleDTO;
import com.ideality.coreflow.calendar.query.service.CalendarQueryFacadeService;
import com.ideality.coreflow.common.response.APIResponse;

import lombok.RequiredArgsConstructor;

@RestController("calendarQueryController")
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarQueryController {

	private final CalendarQueryFacadeService calendarQueryFacadeService;

	// TODO. 내가 속한 부서의 프로젝트 일정 가져오기 (세부일정)
	// @GetMapping("/dept")
	// public ResponseEntity<APIResponse<List<ResponseScheduleDTO>>> getDeptScheduleList() {
	//
	// 	List<ResponseScheduleDTO> response = calendarQueryFacadeService.getDeptScheduleList();
	// 	return ResponseEntity.ok(APIResponse.success(response, "부서 일정 목록 조회 성공"));
	//
	// }



	// TODO. 개인 일정 목록 조회
	@GetMapping("/personal")
	public ResponseEntity<APIResponse<List<ResponseScheduleDTO>>> getPersonalScheduleDetail(@RequestBody RequestUserDTO requestDTO) {
		// TODO. 로그인한 사용자의 아이디 가져오기
		Long userId = 1L;
		List<ResponseScheduleDTO> personalSchedules = calendarQueryFacadeService.getAllPersonalSchedule(requestDTO.getUserId());

		return ResponseEntity.ok(APIResponse.success(personalSchedules, "개인 일정 목록 조회 성공 ✅"));
	}

	// TODO. 개인 일정 상세 조회
	@GetMapping("/personal/{taskId}")
	public ResponseEntity<APIResponse<ResponseScheduleDTO>> getAllPersonalSchedule(
		@RequestBody RequestUserDTO requestDTO,
		@PathVariable Long taskId)
	{
		// TODO. 로그인한 사용자의 아이디 가져오기
		ResponseScheduleDTO response = calendarQueryFacadeService.getPersonalDetail(requestDTO.getUserId(), taskId);

		return ResponseEntity.ok(APIResponse.success(response, "개인 일정 상세 조회 성공 ✅"));
	}



}
