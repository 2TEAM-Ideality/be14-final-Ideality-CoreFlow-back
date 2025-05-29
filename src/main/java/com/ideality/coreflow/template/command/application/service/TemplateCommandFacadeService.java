package com.ideality.coreflow.template.command.application.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideality.coreflow.attachment.command.application.service.AttachmentCommandService;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.service.S3Service;
import com.ideality.coreflow.template.command.application.dto.RequestCreateTemplateDTO;
import com.ideality.coreflow.template.command.domain.aggregate.Template;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateCommandFacadeService {

	private final TemplateCommandService templateCommandService;
	private final AttachmentCommandService attachmentCommandService;
	private final S3Service s3Service;
	private final ObjectMapper objectMapper;	// Jackson 라이브러리 제공 클래스 (Json 직렬화, 역직렬화에서 사용)

	@Transactional
	public void createTemplate(RequestCreateTemplateDTO requestDTO) {

		// 1. 템플릿 생성
		Template template = templateCommandService.createTemplate(requestDTO);

		// 2. JSON 직렬화
		String json = serializeJsonOrThrow(requestDTO);

		// 3. 파일명 및 폴더 경로 지정
		String fileName = template.getId() + ".json";
		String folder = "template-json";

		// 4. S3에 업로드
		String fileUrl = uploadToS3OrThrow(json, folder, fileName);

		// 5. AttachmentEntity 생성 및 DB 저장
		attachmentCommandService.createAttachmentForTemplate(
			template.getId(), fileName, fileUrl, requestDTO.getCreatedBy(), json
		);
	}

	// S3 업로드
	private String uploadToS3OrThrow(String json, String folder, String fileName) {
		try {
			return s3Service.uploadJson(json, folder, fileName);
		} catch (Exception e) {
			log.error("S3 업로드 실패", e);
			throw new BaseException(ErrorCode.S3_UPLOAD_FAILED);
		}
	}


	// JSON 직렬화
	private String serializeJsonOrThrow(RequestCreateTemplateDTO requestDTO) {
		try {
			return objectMapper.writeValueAsString(Map.of(
				"nodeList", requestDTO.getNodeList(),
				"edgeList", requestDTO.getEdgeList()
			));
		} catch (JsonProcessingException e) {
			log.error("JSON 직렬화 실패", e);
			throw new BaseException(ErrorCode.JSON_SERIALIZATION_ERROR);
		}
	}

}
