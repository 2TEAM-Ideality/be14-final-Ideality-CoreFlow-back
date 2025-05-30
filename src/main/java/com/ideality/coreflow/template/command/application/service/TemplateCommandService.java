package com.ideality.coreflow.template.command.application.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.template.command.application.dto.RequestCreateTemplateDTO;
import com.ideality.coreflow.template.command.domain.aggregate.Template;
import com.ideality.coreflow.template.command.domain.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateCommandService {

	private final TemplateRepository templateRepository;

	@Transactional
	public Template createTemplate(RequestCreateTemplateDTO requestDTO) {

		// 1. 템플릿 DB 저장
		Template newTemplate = Template.builder()
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

		return newTemplate;
	}

	public Template updateTemplateInfo(Long templateId, String name, String description, int duration, int taskCount) {
		Template originTemplate = templateRepository.findById(templateId).
			orElseThrow(() -> new BaseException(ErrorCode.TEMPLATE_NOT_FOUND));

		originTemplate.updateTemplate(name, description, duration, taskCount);
		return originTemplate;
	}

	public void deleteTemplate(Long templateId) {
		Template originTemplate = templateRepository.findById(templateId)
			.orElseThrow(() -> new BaseException(ErrorCode.TEMPLATE_NOT_FOUND));

		originTemplate.deleteTemplate();

	}
}
