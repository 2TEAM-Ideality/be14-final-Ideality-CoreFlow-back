package com.ideality.coreflow.template.query.service;

import java.util.List;

import com.ideality.coreflow.template.query.dto.RequestTemplateDetailDTO;
import com.ideality.coreflow.template.query.dto.ResponseTemplateListDTO;

public interface TemplateQueryService {

	List<ResponseTemplateListDTO> getAllTemplates();

	RequestTemplateDetailDTO getTemplateDetail(Long templateId);
}
