package com.ideality.coreflow.template.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.template.dto.TemplateDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/template")
@RequiredArgsConstructor
public class TemplateController {

	// TODO. 템플릿 목록 조회
	@GetMapping("/")
	public ResponseEntity<APIResponse<TemplateDTO>> getTemplates(){

	}


	// TODO. 템플릿 상세 조회


	// TODO. 템플릿 생성


	// TODO. 템플릿 수정


	// TODO. 템플릿 삭제



}
