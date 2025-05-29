package com.ideality.coreflow.attachment.command.application.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.attachment.command.domain.aggregate.Attachment;
import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import com.ideality.coreflow.attachment.command.domain.repository.AttachmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentCommandService {

	private final AttachmentRepository attachmentRepository;

	@Transactional
	public void createAttachmentForTemplate(Long templateId, String fileName, String fileUrl, Long createdBy, String json) {
		Attachment attachment = Attachment.builder()
			.originName("template.json")
			.storedName(fileName)
			.url(fileUrl)
			.fileType("application/json")
			.size(String.valueOf(json.getBytes(StandardCharsets.UTF_8).length))
			.uploadAt(LocalDateTime.now())
			.targetType(FileTargetType.TEMPLATE)
			.targetId(templateId)
			.uploaderId(createdBy)
			.build();
		attachmentRepository.save(attachment);
	}
}
