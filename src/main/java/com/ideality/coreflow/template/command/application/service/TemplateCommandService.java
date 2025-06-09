package com.ideality.coreflow.template.command.application.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.template.command.application.dto.RequestCreateTemplateDTO;
import com.ideality.coreflow.template.command.application.dto.RequestTemplateByProjectDTO;
import com.ideality.coreflow.template.command.domain.aggregate.Template;
import com.ideality.coreflow.template.command.domain.aggregate.TemplateDept;
import com.ideality.coreflow.template.command.domain.repository.TemplateDeptRepository;
import com.ideality.coreflow.template.command.domain.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateCommandService {

	private final TemplateRepository templateRepository;
	private final TemplateDeptRepository templateDeptRepository;

	// 템플릿 정보 생성
	@Transactional
	public Template createTemplate(RequestCreateTemplateDTO requestDTO) {
		// 템플릿 이름 중복 시 예외 처리

		// 1. 템플릿 DB 저장
		Template newTemplate = Template.builder()
			.name(requestDTO.getName())
			.description(requestDTO.getDescription())
			.createdAt(LocalDateTime.now())
			.createdBy(requestDTO.getCreatedBy())
			.duration(requestDTO.getDuration())
			.taskCount(requestDTO.getTaskCount())
			.build();

		templateRepository.save(newTemplate);

		return newTemplate;
	}

	// 프로젝트 기반 템플릿 정보 생성
	@Transactional
	public Template createTemplateByProject(RequestTemplateByProjectDTO requestDTO, int taskCount, int durtaion) {
		Template newTemplate = Template.builder()
			.name(requestDTO.getName())
			.description(requestDTO.getDescription())
			.createdAt(LocalDateTime.now())
			.createdBy(requestDTO.getCreatedBy())
			.duration(durtaion)
			.taskCount(taskCount)
			.build();
		templateRepository.save(newTemplate);

		return newTemplate;
	}

	// 템플릿 정보 수정
	@Transactional
	public void updateTemplateInfo(Long templateId, String name, String description, int duration, int taskCount, Long updatedBy) {
		Template originTemplate = templateRepository.findById(templateId).
				orElseThrow(() -> new BaseException(ErrorCode.TEMPLATE_NOT_FOUND));

		originTemplate.updateTemplate(name, description, duration, taskCount, updatedBy);
	}

	@Transactional
	public void deleteTemplate(Long templateId) {
		Template originTemplate = templateRepository.findById(templateId)
			.orElseThrow(() -> new BaseException(ErrorCode.TEMPLATE_NOT_FOUND));

		originTemplate.deleteTemplate();

	}

	@Transactional
	// TEMPLATE_DEPT 테이블 저장 설명. TemplateDeptService 로 따로 분리해야 할 지 ?
	public void saveTemplateDept(Long templateId, Long deptId) {
		log.info("Saving template dept {}", deptId);
		TemplateDept dept = TemplateDept.builder()
			.deptId(deptId)
			.templateId(templateId)
			.build();
		templateDeptRepository.save(dept);
	}

	@Transactional
	public void deleteAllTemplateDepts(Long templateId) {
		templateDeptRepository.deleteByTemplateId(templateId);
	}


}
