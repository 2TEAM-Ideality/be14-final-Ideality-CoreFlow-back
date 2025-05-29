package com.ideality.coreflow.template.query.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.template.query.dto.ResponseTemplateDetailDTO;
import com.ideality.coreflow.template.query.dto.TemplateInfoDTO;
import com.ideality.coreflow.template.query.dto.ResponseTemplateListDTO;
import com.ideality.coreflow.template.query.service.TemplateQueryFacadeService;
import com.ideality.coreflow.template.query.service.TemplateQueryService;

import lombok.RequiredArgsConstructor;

@RestController("templateQueryController")
@RequestMapping("/api/template")
@RequiredArgsConstructor
public class TemplateController {

	private final TemplateQueryFacadeService templateQueryFacadeService;
	private final TemplateQueryService templateQueryService;


	// TODO. 템플릿 목록 조회
	@GetMapping("/list")
	public ResponseEntity<APIResponse<List<ResponseTemplateListDTO>>> getTemplates(){
		List<ResponseTemplateListDTO> templates = templateQueryService.getAllTemplates();
		return ResponseEntity.ok(
			APIResponse.success(templates, "템플릿 목록 조회 성공 ✅")
		);
	}

	// TODO. 템플릿 상세 조회
	@GetMapping("/{templateId}")
	public ResponseEntity<APIResponse<ResponseTemplateDetailDTO>> getTemplateDetail(@PathVariable("templateId") Long templateId){
		ResponseTemplateDetailDTO template = null;
		try {
			template = templateQueryFacadeService.getTemplateDetail(templateId);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		if(template == null){
			throw new BaseException(ErrorCode.TEMPLATE_NOT_FOUND);
		}

		return ResponseEntity.ok(
			APIResponse.success(template, "템플릿 상세 정보 조회 성공 ✅")
		);
	}



}
