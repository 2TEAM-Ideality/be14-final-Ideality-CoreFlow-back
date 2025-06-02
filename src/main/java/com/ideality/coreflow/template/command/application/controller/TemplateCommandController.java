package com.ideality.coreflow.template.command.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.template.command.application.dto.RequestUpdateTemplateDTO;
import com.ideality.coreflow.template.command.application.service.TemplateCommandFacadeService;
import com.ideality.coreflow.template.command.application.dto.RequestCreateTemplateDTO;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/template")
@RestController("templateCommandController")
@RequiredArgsConstructor
public class TemplateCommandController {

	private final TemplateCommandFacadeService templateCommandFacadeService;

	// POST 템플릿 생성
	@PostMapping("")
	public ResponseEntity<APIResponse<?>> createTemplate(@RequestBody RequestCreateTemplateDTO requestDTO){
		templateCommandFacadeService.createTemplate(requestDTO);
		return ResponseEntity.ok(
			APIResponse.success(null, "템플릿이 성공적으로 생성되었습니다.✅")
		);
	}


	// 템플릿 수정
	@PutMapping("/{templateId}")
	public ResponseEntity<APIResponse<?>> updateTemplate(
		@PathVariable Long templateId,
		@RequestBody RequestUpdateTemplateDTO requestDTO) {

		templateCommandFacadeService.updateTemplate(templateId, requestDTO);
		return ResponseEntity.ok(
			APIResponse.success(null, "템플릿이 성공적으로 수정되었습니다.✅")
		);
	}

	// 템플릿 삭제
	@DeleteMapping("/{templateId}")
	public ResponseEntity<APIResponse<?>> deleteTemplate(
		@PathVariable Long templateId
	){
		templateCommandFacadeService.deleteTemplate(templateId);
		return ResponseEntity.ok(
			APIResponse.success(null, "템플릿이 성공적으로 삭제되었습니다.✅")
		);
	}

}
