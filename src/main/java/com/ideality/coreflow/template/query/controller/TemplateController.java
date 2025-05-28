package com.ideality.coreflow.template.query.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.template.query.dto.TemplateDetailDTO;
import com.ideality.coreflow.template.query.dto.TemplateListResponseDTO;
import com.ideality.coreflow.template.query.service.TemplateQueryService;

import lombok.RequiredArgsConstructor;

@RestController("templateQueryController")
@RequestMapping("/api/template")
@RequiredArgsConstructor
public class TemplateController {

	private final TemplateQueryService templateQueryService;

	// TODO. 템플릿 목록 조회
	@GetMapping("/list")
	public ResponseEntity<APIResponse<List<TemplateListResponseDTO>>> getTemplates(){
		List<TemplateListResponseDTO> templates = templateQueryService.getAllTemplates();
		return ResponseEntity.ok(
			APIResponse.success(templates, "템플릿 목록 조회 성공 ✅")
		);
	}

	// TODO. 템플릿 상세 조회
	@GetMapping("/{templateId}")
	public ResponseEntity<APIResponse<TemplateDetailDTO>> getTemplateDetail(@PathVariable("templateId") Long templateId){
		TemplateDetailDTO template = templateQueryService.getTemplateDetail(templateId);

		if(template == null){
			throw new BaseException(ErrorCode.TEMPLATE_NOT_FOUND);
		}
		String message = String.format("%d번 템플릿 상세 정보 조회 성공 ✅", templateId);
		return ResponseEntity.ok(
			APIResponse.success(template, "템플릿 상세 정보 조회 성공 ✅")
		);
	}



}
