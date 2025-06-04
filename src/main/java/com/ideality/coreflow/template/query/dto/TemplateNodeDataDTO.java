package com.ideality.coreflow.template.query.dto;

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
	private List<Integer> deptList; 	// 참여 부서 목록
	private int slackTime; 				// 슬랙 타임
	private String startBaseLine;		// 시작 베이스라인
	private String endBaseLine;			// 마감 베이스라인
}