package com.ideality.coreflow.calendar.query.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.calendar.query.dto.ResponseScheduleDTO;
import com.ideality.coreflow.calendar.query.service.CalendarService;
import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.template.query.dto.DeptDTO;
import com.ideality.coreflow.user.query.service.UserQueryService;

import lombok.RequiredArgsConstructor;

@RestController("calendarQueryController")
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarQueryController {
	// private final TaskQueryService taskQueryService;
	private final CalendarService calendarService;
	private final UserQueryService userQueryService;
	private final DeptQueryService deptQueryService;

	// TODO. 내가 속한 부서의 프로젝트 일정 가져오기 (세부일정)
	//
	@GetMapping("/dept")
	public ResponseEntity<APIResponse<List<ResponseScheduleDTO>>> getDeptScheduleList() {
		//

		Long userId = 1L;
		String deptName = userQueryService.getDeptNameByUserId(userId);
		Long deptId = deptQueryService.findDeptIdByName(deptName);

		List<ResponseScheduleDTO> response = calendarService.getAllDeptSchedule(deptId);
		return ResponseEntity.ok(APIResponse.success(response, "부서 일정 목록 조회 성공"));

	}



	// TODO. 개인 일정 목록 조회
	@GetMapping("/personal")
	public ResponseEntity<APIResponse<List<ResponseScheduleDTO>>> getPersonalSchedule() {
		// TODO. 로그인한 사용자의 아이디 가져오기
		Long userId = 1L;
		List<ResponseScheduleDTO> personalSchedules = calendarService.getAllPersonalSchedule(userId);

		return ResponseEntity.ok(APIResponse.success(personalSchedules, "개인 일정 목록 조회 성공 ✅"));
	}

	// TODO. 개인 일정 상세 조회
	@GetMapping("/personal/{taskId}")
	public ResponseEntity<APIResponse<ResponseScheduleDTO>> getPersonalSchedule(@PathVariable Long taskId) {
		// TODO. 로그인한 사용자의 아이디 가져오기
		Long memberId = 1L;
		ResponseScheduleDTO response = calendarService.getPersonalDetail(memberId, taskId);

		return ResponseEntity.ok(APIResponse.success(response, "개인 일정 상세 조회 성공 ✅"));
	}



}
