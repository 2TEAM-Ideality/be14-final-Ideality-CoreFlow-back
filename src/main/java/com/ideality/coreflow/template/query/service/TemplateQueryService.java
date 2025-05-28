package com.ideality.coreflow.template.query.service;

import java.util.List;

import com.ideality.coreflow.template.query.dto.TemplateDetailDTO;
import com.ideality.coreflow.template.query.dto.TemplateListResponseDTO;

public interface TemplateQueryService {

	List<TemplateListResponseDTO> getAllTemplates();

	TemplateDetailDTO getTemplateDetail(Long templateId);
}
