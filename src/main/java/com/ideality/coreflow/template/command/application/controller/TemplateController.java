package com.ideality.coreflow.template.command.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.template.command.application.service.TemplateCommandService;
import com.ideality.coreflow.template.command.application.dto.RequestCreateTemplateDTO;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/template")
@RestController("templateCommandController")
@RequiredArgsConstructor
public class TemplateController {

	private final TemplateCommandService templateCommandService;

	// TODO. 템플릿 생성
	@PostMapping("")
	public ResponseEntity<APIResponse<?>> createTemplate(@RequestBody RequestCreateTemplateDTO requestDTO){
		templateCommandService.createTemplate(requestDTO);
		return ResponseEntity.ok(
			APIResponse.success(null, "템플릿이 성공적으로 생성되었습니다.✅")
		);
	}


	// TODO. 템플릿 수정


	// TODO. 템플릿 삭제

}
