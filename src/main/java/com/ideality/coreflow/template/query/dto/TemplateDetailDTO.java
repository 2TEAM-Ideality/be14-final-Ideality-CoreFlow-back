package com.ideality.coreflow.template.query.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateDetailDTO {
	// 템플릿 json 파일 조회
	private TemplateInfoDTO templateInfo;

	private Map<String, Object> templateData;	// 노드/엣지 데이터

}
