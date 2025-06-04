package com.ideality.coreflow.template.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseCreateTemplateDTO {
	private Long templateId;
	private String templateName;
	private Long projectId;
}
