package com.ideality.coreflow.template.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemplateNodeDTO {

	private String id; 					// VueFlow에선 문자열 ID 사용을 권장한다고 함.
	private String type; 				// ex) "custom"
	private PositionDTO position; 		// x, y 좌표
	private TemplateNodeDataDTO data; 	// 노드 데이터 내용
}


