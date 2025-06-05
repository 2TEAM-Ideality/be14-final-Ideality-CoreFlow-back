package com.ideality.coreflow.template.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EdgeDTO {
	private String id; 		//
	private String source; 	// 출발 노드 id
	private String target; 	// 도착 노드 id
	private String type; 	// ex) "default"
}
