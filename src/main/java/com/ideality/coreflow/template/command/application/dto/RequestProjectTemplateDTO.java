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
public class RequestProjectTemplateDTO {

	private Long projectId;
	private Long createdBy;
	private String name;
	private String description;

}
