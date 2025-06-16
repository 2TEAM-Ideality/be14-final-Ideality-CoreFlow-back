package com.ideality.coreflow.attachment.query.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.attachment.query.dto.ReportAttachmentDTO;
import com.ideality.coreflow.attachment.query.service.AttachmentQueryService;
import com.ideality.coreflow.common.response.APIResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class AttachmentQueryController {

	private final AttachmentQueryService attachmentQueryService;

	// 프로젝트의 산출물 목록 조회하기
	@GetMapping("/project/{projectId}/attachment/list")
	public ResponseEntity<APIResponse<List<ReportAttachmentDTO>>> getAttachmentsByProjectId(@PathVariable Long projectId) {
		log.info("프로젝트 {}에 대한 산출물 가져오기", projectId);

		List<ReportAttachmentDTO> response = attachmentQueryService.getAttachmentsByProjectId(projectId);

		return ResponseEntity.ok(APIResponse.success(response, "프로젝트에 대한 산출물 조회 성공"));

	}
}
