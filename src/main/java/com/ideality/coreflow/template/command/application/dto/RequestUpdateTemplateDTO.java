package com.ideality.coreflow.template.command.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ideality.coreflow.template.query.dto.EdgeDTO;
import com.ideality.coreflow.template.query.dto.TemplateInfoDTO;
import com.ideality.coreflow.template.query.dto.TemplateNodeDTO;

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
	private String name;
	private String description;

	private int duration; 		// 총 소요일
	private int taskCount; 	// 전체 태스크 개수

	private LocalDateTime updatedAt;
	private Long updatedBy;

	private List<TemplateNodeDTO> nodeList;
	private List<EdgeDTO> edgeList;

}
