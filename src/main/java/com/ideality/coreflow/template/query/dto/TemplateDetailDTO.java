package com.ideality.coreflow.template.query.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDetailDTO {
	// 템플릿 디테일 정보 조회
	private int id;			// 템플릿 아이디
	private String name;	// 템플릿 이름
	private String description; // 템플릿 설명

	private LocalDateTime createdAt;	// 생성일
	private String createdBy;			// 생성자

	private int duration; 		// 총 소요일
	private int taskCount; 	// 전체 태스크 개수
	private int usingProjects; 	// 사용 중인 프로젝트 개수

	private String url; 		// S3 URL   ex) /templates/6/template-data.json

	// private List<TemplateNodeDTO> nodeList; 	// 노드 리스트
	// private List<EdgeDTO> edgeList;

	// private List<String> deptList; // 참여 부서 목록
}
