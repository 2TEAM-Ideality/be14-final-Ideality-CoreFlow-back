package com.ideality.coreflow.template.command.application.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.template.command.application.dto.RequestTemplateByProjectDTO;
import com.ideality.coreflow.template.command.application.dto.RequestUpdateTemplateDTO;
import com.ideality.coreflow.template.command.application.service.TemplateCommandFacadeService;
import com.ideality.coreflow.template.command.application.dto.RequestCreateTemplateDTO;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/template")
@RestController("templateCommandController")
@RequiredArgsConstructor
@Slf4j
public class TemplateCommandController {

	private final TemplateCommandFacadeService templateCommandFacadeService;

	// POST 템플릿 생성
	@PostMapping("")
	public ResponseEntity<APIResponse<?>> createTemplate(@RequestBody RequestCreateTemplateDTO requestDTO){
		log.info("Create template request: {}", requestDTO.getName());
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

		log.info("Update template {}", templateId);
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
		log.info("Delete template {}", templateId);
		templateCommandFacadeService.deleteTemplate(templateId);
		return ResponseEntity.ok(
			APIResponse.success(null, "템플릿이 성공적으로 삭제되었습니다.✅")
		);
	}

	// 프로젝트 템플릿화
	@PostMapping("/project")
	public ResponseEntity<APIResponse<?>> createTemplateByProject(@RequestBody RequestTemplateByProjectDTO requestDTO){
		log.info("ID-{}-프로젝트에 대한 템플릿화 요청 들어옴:", requestDTO.getProjectId());
		templateCommandFacadeService.createTemplateByProject(requestDTO);

		return ResponseEntity.ok(
			APIResponse.success(null, "템플릿이 성공적으로 생성되었습니다.✅")
		);
	}



}