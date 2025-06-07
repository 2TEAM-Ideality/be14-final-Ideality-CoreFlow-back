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
public class RequestTemplateByProjectDTO {
	// 프로젝트 기반 템플릿 생성 요청
	private Long projectId;
	private Long createdBy;
	private String name;
	private String description;

}
