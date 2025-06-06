package com.ideality.coreflow.template.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.template.query.dto.TemplateInfoDTO;
import com.ideality.coreflow.template.query.mapper.TemplateMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateQueryService{

	private final TemplateMapper templateMapper;

	public List<TemplateInfoDTO> getAllTemplates() {
		return templateMapper.selectAllTemplates();
	}

	public TemplateInfoDTO getTemplateDetail(Long templateId) {
		return templateMapper.selectTemplateDetail(templateId);
	}
}
