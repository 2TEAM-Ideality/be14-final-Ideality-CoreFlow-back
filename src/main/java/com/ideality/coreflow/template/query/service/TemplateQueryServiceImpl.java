package com.ideality.coreflow.template.query.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ideality.coreflow.template.query.dto.TemplateDetailDBDTO;
import com.ideality.coreflow.template.query.dto.TemplateListResponseDTO;
import com.ideality.coreflow.template.query.mapper.TemplateMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TemplateQueryServiceImpl  implements TemplateQueryService{

	private final TemplateMapper templateMapper;

	@Autowired
	public TemplateQueryServiceImpl(TemplateMapper templateMapper) {
		this.templateMapper = templateMapper;
	}

	@Override
	public List<TemplateListResponseDTO> getAllTemplates() {
		return templateMapper.selectAllTemplates();
	}

	@Override
	public TemplateDetailDBDTO getTemplateDetail(Integer templateId) {
		// if (detail == null) {
		// 	throw new BaseException(ErrorCode.TEMPLATE_NOT_FOUND);
		// }
		return templateMapper.selectTemplateDetail(templateId);
	}
}
