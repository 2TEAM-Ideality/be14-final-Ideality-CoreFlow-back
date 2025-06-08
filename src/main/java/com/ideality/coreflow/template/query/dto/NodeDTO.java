package com.ideality.coreflow.template.query.dto;

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
public class NodeDTO {

	private String id; 					// VueFlow에선 문자열 ID 사용을 권장한다고 함.
	private String type; 				// ex) "custom"
	private NodeDataDTO data; 	// 노드 데이터 내용
}


