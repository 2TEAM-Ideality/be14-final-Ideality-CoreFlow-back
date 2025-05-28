package com.ideality.coreflow.template.query.dto;

import java.time.LocalDateTime;
import java.util.List;

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
public class TemplateNodeDataDTO {

	private String label; 			// 태스크명
	private String description; 	// 설명
	private List<String> deptList; 	// 참여 부서 목록	
	private LocalDateTime startBaseLine;		// 시작 베이스라인
	private LocalDateTime endBaseLine;			// 마감 베이스라인
}
