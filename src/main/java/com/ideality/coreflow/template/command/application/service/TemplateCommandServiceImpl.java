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

	@Override
	@Transactional
	public void createTemplate(RequestCreateTemplateDTO requestDTO) {

		// 1. 템플릿 DB 저장
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
		templateRepository.save(newTemplate);

		Long templateId = newTemplate.getId();		// 새로 생성된 템플릿 아이디

		// 2. JSON 문자열 생성
		String jsonString = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonString = mapper.writeValueAsString(
				Map.of("nodeList", requestDTO.getNodeList(), "edgeList", requestDTO.getEdgeList())
			);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		// 3. 업로드용 파일명 및 폴더 지정
		String templateIdFileName = templateId + ".json";
		String folder = "template-json";

		// 4. S3에 업로드
		String fileUrl = s3Service.uploadJson(jsonString, folder, templateIdFileName);

		// 5. DB 저장 (AttachmentEntity 생성)
		AttachmentEntity newFile = AttachmentEntity.builder()
			.originName("template.json") // 사용자에게 보일 이름 (원본명)
			.storedName(templateIdFileName) // 실제 저장된 이름
			.url(fileUrl)
			.fileType("application/json")
			.size(String.valueOf(jsonString.getBytes(StandardCharsets.UTF_8).length))
			.uploadAt(LocalDateTime.now())
			.targetType(FileTargetType.TEMPLATE)
			.targetId(newTemplate.getId())
			.uploaderId(requestDTO.getCreatedBy())
			.build();

		attachmentRepository.save(newFile);

	}
}
