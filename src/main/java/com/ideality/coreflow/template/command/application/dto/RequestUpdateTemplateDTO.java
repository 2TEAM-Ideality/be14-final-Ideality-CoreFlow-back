package com.ideality.coreflow.template.command.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ideality.coreflow.template.query.dto.EdgeDTO;
import com.ideality.coreflow.template.query.dto.NodeDTO;

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
public class RequestUpdateTemplateDTO {

	// 템플릿 수정 요청
	private String name;			// 템플릿 이름 
	private String description;		// 설명
	
	private int duration; 			// 총 소요일
	private int taskCount; 			// 전체 태스크 개수
	private Long updatedBy;			// 수정자

	// 수정 템플릿 데이터
	private List<NodeDTO> nodeList;
	private List<EdgeDTO> edgeList;

}
