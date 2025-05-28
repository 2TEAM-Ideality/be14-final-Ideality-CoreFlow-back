package com.ideality.coreflow.template.query.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ideality.coreflow.template.query.dto.TemplateListResponseDTO;
import com.ideality.coreflow.template.query.mapper.TemplateMapper;

@Service
public class TemplateQueryServiceImpl  implements TemplateQueryService{

	private TemplateMapper templateMapper;

	@Autowired
	public TemplateQueryServiceImpl(TemplateMapper templateMapper) {
		this.templateMapper = templateMapper;
	}

	@Override
	public List<TemplateListResponseDTO> getAllTemplates() {
		return List.of();
	}
}
