package com.ideality.coreflow.template.query.controller;

import java.util.List;

import org.hibernate.sql.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.template.query.dto.TemplateListResponseDTO;
import com.ideality.coreflow.template.query.service.TemplateQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/template")
@RequiredArgsConstructor
public class TemplateController {

	private TemplateQueryService templateQueryService;

	@Autowired
	public TemplateController(TemplateQueryService templateQueryService) {
		this.templateQueryService = templateQueryService;
	}

	// TODO. 템플릿 목록 조회
	@GetMapping("/list")
	public ResponseEntity<APIResponse<TemplateListResponseDTO>> getTemplates(){

		List<TemplateListResponseDTO> templates = templateQueryService.getAllTemplates();

		return ResponseEntity.ok(
			APIResponse.success((TemplateListResponseDTO)templates)
		);

	}


	// TODO. 템플릿 상세 조회





}
