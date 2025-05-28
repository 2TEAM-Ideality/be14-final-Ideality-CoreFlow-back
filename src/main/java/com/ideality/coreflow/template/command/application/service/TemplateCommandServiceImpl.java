package com.ideality.coreflow.template.command.application.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideality.coreflow.attachment.command.domain.aggregate.AttachmentEntity;
import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import com.ideality.coreflow.attachment.command.domain.repository.AttachmentRepository;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.service.S3Service;
import com.ideality.coreflow.template.command.domain.aggregate.RequestCreateTemplateDTO;
import com.ideality.coreflow.template.command.domain.aggregate.TemplateEntity;
import com.ideality.coreflow.template.command.domain.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateCommandServiceImpl implements TemplateCommandService {

	private final TemplateRepository templateRepository;
	private final AttachmentRepository attachmentRepository;
	private final S3Service s3Service;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	@Transactional
	public void createTemplate(RequestCreateTemplateDTO requestDTO) {

		// 1. 템플릿 DB 저장
		TemplateEntity newTemplate = buildTemplateEntity(requestDTO);
		saveTemplateOrThrow(newTemplate);

		// 2. JSON 직렬화
		String json = serializeJsonOrThrow(requestDTO);

		// 3. 파일명 및 폴더 경로 지정
		String fileName = newTemplate.getId() + ".json";
		String folder = "template-json";

		// 4. S3에 업로드
		String fileUrl = uploadToS3OrThrow(json, folder, fileName);

		// 5. DB 저장 (AttachmentEntity 생성)
		// 5. 첨부파일 엔티티 저장
		AttachmentEntity attachment = buildAttachmentEntity(
			newTemplate,
			fileName,
			fileUrl,
			requestDTO.getCreatedBy(),
			json);
		saveAttachmentOrThrow(attachment);

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

	// ReuqestDTO -> TemplateEntity
	private TemplateEntity buildTemplateEntity(RequestCreateTemplateDTO requestDTO) {
		TemplateEntity newTemplate = TemplateEntity.builder()
			.name(requestDTO.getName())
			.description(requestDTO.getDescription())
			.createdAt(LocalDateTime.now())
			.createdBy(requestDTO.getCreatedBy())
			.updatedAt(LocalDateTime.now())
			.updatedBy(requestDTO.getCreatedBy())
			.taskCount(requestDTO.getTaskCount())
			.duration(requestDTO.getDuration())
			.isDeleted(false)
			.build();
		return newTemplate;
	}

	// JSON ->  AttachmentEntity
	private AttachmentEntity buildAttachmentEntity(TemplateEntity template, String fileName, String url, Long uploaderId, String content) {
		return AttachmentEntity.builder()
			.originName("template.json")
			.storedName(fileName)
			.url(url)
			.fileType("application/json")
			.size(String.valueOf(content.getBytes(StandardCharsets.UTF_8).length))
			.uploadAt(LocalDateTime.now())
			.targetType(FileTargetType.TEMPLATE)
			.targetId(template.getId())
			.uploaderId(uploaderId)
			.build();
	}

	// Template DB 저장
	private void saveTemplateOrThrow(TemplateEntity newTemplate) {
		try {
			templateRepository.save(newTemplate);
		} catch (Exception e) {
			log.error("템플릿 저장 실패", e);
			throw new BaseException(ErrorCode.DATABASE_ERROR);
		}
	}

	// 첨부 파일 DB 저장
	private void saveAttachmentOrThrow(AttachmentEntity attachment) {
		try {
			attachmentRepository.save(attachment);
		} catch (Exception e) {
			log.error("첨부파일 DB 저장 실패", e);
			throw new BaseException(ErrorCode.DATABASE_ERROR);
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
