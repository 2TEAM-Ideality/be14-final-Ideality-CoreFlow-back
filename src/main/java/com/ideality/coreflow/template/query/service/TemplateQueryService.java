package com.ideality.coreflow.template.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.attachment.query.mapper.AttachmentMapper;
import com.ideality.coreflow.template.query.dto.RequestTemplateDetailDTO;
import com.ideality.coreflow.template.query.dto.ResponseTemplateListDTO;
import com.ideality.coreflow.template.query.mapper.TemplateMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateQueryService{

	private final TemplateMapper templateMapper;

	public List<ResponseTemplateListDTO> getAllTemplates() {
		return templateMapper.selectAllTemplates();
	}

	public RequestTemplateDetailDTO getTemplateDetail(Long templateId) {
		RequestTemplateDetailDTO first = templateMapper.selectTemplateDetail(templateId);

		return first;
	}
}
