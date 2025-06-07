package com.ideality.coreflow.template.query.service;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import com.ideality.coreflow.attachment.query.service.AttachmentQueryService;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.s3.S3Service;
import com.ideality.coreflow.template.query.dto.ResponseTemplateDetailDTO;
import com.ideality.coreflow.template.query.dto.TemplateInfoDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateQueryFacadeService {

	private final TemplateQueryService templateQueryService;
	private final AttachmentQueryService attachmentQueryService;
	private final S3Service s3Service;
	private final ObjectMapper objectMapper;

	public ResponseTemplateDetailDTO getTemplateDetail(Long templateId) throws JsonProcessingException {

		// 1. 템플릿 메타 정보 조회
		TemplateInfoDTO templateInfo = templateQueryService.getTemplateDetail(templateId);
		if (templateInfo == null) {
			throw new BaseException(ErrorCode.TEMPLATE_NOT_FOUND);
		}

		// 2. 첨부 파일 테이블에서 URL 가져오기
		String templateUrl = attachmentQueryService.getUrl(templateId, FileTargetType.TEMPLATE);

		// 3. s3 에서 json 데이터 가져오기
		String jsonContent = s3Service.getJsonFile(templateUrl);
		Map parsed = objectMapper.readValue(jsonContent, Map.class);

		return ResponseTemplateDetailDTO.builder()
			.templateInfo(templateInfo)
			.templateData(parsed)
			.build();
	}

	public List<TemplateInfoDTO> getAllTemplates() {
		return templateQueryService.getAllTemplates();
	}
}
