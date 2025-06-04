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
public class RequestCreateTemplateDTO {

	// 템플릿 생성 요청
	private String name;
	private String description;
	private Long createdBy;				// 생성자
	private int duration; 				// 총 소요일
	private int taskCount; 				// 전체 태스크 개수

	// 수정 템플릿 데이터
	private List<NodeDTO> nodeList;
	private List<EdgeDTO> edgeList;

}
