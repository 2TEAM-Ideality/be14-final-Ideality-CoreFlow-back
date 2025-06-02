package com.ideality.coreflow.template.command.application.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideality.coreflow.attachment.command.application.service.AttachmentCommandService;
import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.service.S3Service;
import com.ideality.coreflow.template.command.application.dto.RequestCreateTemplateDTO;
import com.ideality.coreflow.template.command.application.dto.RequestUpdateTemplateDTO;
import com.ideality.coreflow.template.command.domain.aggregate.Template;
import com.ideality.coreflow.template.query.dto.TemplateDataDTO;

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

	// 템플릿 생성
	@Transactional
	public void createTemplate(RequestCreateTemplateDTO requestDTO) {

		// 1. 템플릿 생성
		Template template = templateCommandService.createTemplate(requestDTO);

		// 2. JSON 직렬화
		TemplateDataDTO data = TemplateDataDTO.builder()
			.edgeList(requestDTO.getEdgeList())
			.nodeList(requestDTO.getNodeList())
			.build();
		String json = serializeJsonOrThrow(data);
		System.out.println(json);

		// 3. 참여 부서 연결
		// 참여 부서 ID 추출 및 저장
		Set<Long> uniqueDeptIds = requestDTO.getNodeList().stream()
			.flatMap(node -> node.getData().getDeptList().stream()
				.map(Integer::longValue))
			.collect(Collectors.toSet());

		// template_dept 테이블 저장
		for (Long deptId : uniqueDeptIds) {
			templateCommandService.saveTemplateDept(template.getId(), deptId);
		}

		// 4. 파일명 및 폴더 경로 지정
		String fileName = template.getId() + ".json";
		String folder = "template-json";

		// 5. S3에 업로드
		String fileUrl = uploadToS3OrThrow(json, folder, fileName);

		// 6. AttachmentEntity 생성 및 DB 저장
		attachmentCommandService.createAttachmentForTemplate(
			template.getId(), fileName, fileUrl, requestDTO.getCreatedBy(), json
		);
	}

	// 템플릿 수정
	@Transactional
	public void updateTemplate(Long templateId, RequestUpdateTemplateDTO requestDTO) {
		// TODO. 요청자를 updatedBy 로 변경하는 부분 추가해야 함.

		// 1. 템플릿 정보 변경
		templateCommandService.updateTemplateInfo(
			templateId,
			requestDTO.getName(),
			requestDTO.getDescription(),
			requestDTO.getDuration(),
			requestDTO.getTaskCount(),
			requestDTO.getUpdatedBy()
		);

		// 2. Json 직렬화
		TemplateDataDTO data = TemplateDataDTO.builder()
			.edgeList(requestDTO.getEdgeList())
			.nodeList(requestDTO.getNodeList())
			.build();
		String json = serializeJsonOrThrow(data);

		//3. S3 업로드
		String fileName = templateId + ".json";
		String folder = "template-json";
		String fileUrl = uploadToS3OrThrow(json, folder, fileName);
		String size = String.valueOf(json.getBytes(StandardCharsets.UTF_8).length) + " bytes";

		// 4. 첨부 파일 정보 업데이트
		attachmentCommandService.updateAttachmentForTemplate(FileTargetType.TEMPLATE, templateId, fileName, fileUrl, size);

	}

	// 템플릿 삭제
	@Transactional
	public void deleteTemplate(Long templateId) {

		// 1. 템플릿 삭제 여부 변경
		templateCommandService.deleteTemplate(templateId);

		// 2. 첨부파일 삭제 여부 변경
		attachmentCommandService.deleteAttachment(templateId, FileTargetType.TEMPLATE);
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
	private String serializeJsonOrThrow(TemplateDataDTO requestDTO) {
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
