package com.ideality.coreflow.template.command.application.service;

import com.ideality.coreflow.template.command.domain.aggregate.RequestCreateTemplateDTO;

public interface TemplateCommandService {

	void createTemplate(RequestCreateTemplateDTO requestDTO);
}
