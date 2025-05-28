package com.ideality.coreflow.template.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.template.query.dto.TemplateListResponseDTO;

public interface TemplateQueryService {

	List<TemplateListResponseDTO> getAllTemplates();

}
