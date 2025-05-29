package com.ideality.coreflow.template.query.service;


import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import com.ideality.coreflow.attachment.query.service.AttachmentQueryService;
import com.ideality.coreflow.infra.service.S3Service;
import com.ideality.coreflow.template.query.dto.ResponseTemplateDetailDTO;
import com.ideality.coreflow.template.query.dto.TemplateInfoDTO;
import com.ideality.coreflow.template.query.mapper.TemplateMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateQueryFacadeService {

	TemplateMapper templateMapper;
	AttachmentQueryService attachmentQueryService;
	S3Service s3Service;
	ObjectMapper objectMapper;

	public ResponseTemplateDetailDTO getTemplateDetail(Long templateId) throws JsonProcessingException {

		// 1. 템플릿 메타 정보 조회
		TemplateInfoDTO templateInfo = templateMapper.selectTemplateDetail(templateId);
		if (templateInfo == null) {
			throw new RuntimeException("템플릿을 찾을 수 없습니다.");
		}


		// 2. 첨부 파일 테이블에서 URL 가져오기
		String templateUrl = attachmentQueryService.getUrl(templateId, FileTargetType.TEMPLATE);

		// 3. s3 에서 json 데이터 가져오기
		String jsonContent = s3Service.getJsonFile(templateUrl);
		Map<String, Object> parsed = objectMapper.readValue(jsonContent, Map.class);

		ResponseTemplateDetailDTO reseponseTemplateDetailDTO = ResponseTemplateDetailDTO.builder()
			.templateInfo(templateInfo)
			.templateData(parsed).build();


		return reseponseTemplateDetailDTO;


	}
}
