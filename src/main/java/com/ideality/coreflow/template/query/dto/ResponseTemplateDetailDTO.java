package com.ideality.coreflow.template.query.dto;

import java.time.LocalDateTime;
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
public class ResponseTemplateDetailDTO {
	// 템플릿 상세 정보 조회
	private TemplateInfoDTO templateInfo;		// 메타 정보

	private Map<String, Object> templateData;	// 노드/엣지 데이터 


}
