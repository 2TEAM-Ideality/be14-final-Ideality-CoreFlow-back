package com.ideality.coreflow.template.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class TemplateDTO {
	
	private int id;			// 템플릿 아이디
	private String name;	// 템플릿 이름
	private String description; // 템플릿 설명
	private String template;
	private List<TemplateNodeDTO> nodeList; 	// 노드 리스트
	private List<EdgeDTO> edgeList; 			// 엣지 리스트

}
