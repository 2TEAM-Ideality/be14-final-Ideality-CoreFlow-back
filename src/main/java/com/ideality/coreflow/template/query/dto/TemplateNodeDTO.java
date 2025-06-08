package com.ideality.coreflow.template.query.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateNodeDTO {
    private String id; 					// VueFlow에선 문자열 ID 사용을 권장한다고 함.
    private String type; 				// ex) "custom"
    private TemplateNodeDataDTO data; 	// 노드 데이터 내용
}
