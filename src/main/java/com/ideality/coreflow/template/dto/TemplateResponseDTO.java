package com.ideality.coreflow.template.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class TemplateResponseDTO {
	
	private int id;			// 템플릿 아이디
	private String name;	// 템플릿 이름
	private String description; // 템플릿 설명

	private LocalDateTime createdAt;	// 생성일
	private String createdBy;			// 생성자

	private int durtaion; 		// 총 소요일
	private int totalTasks; 	// 전체 태스크 개수
	private int usingProjects; 	// 사용 중인 프로젝트 개수

	private List<TemplateNodeDTO> nodeList; 	// 노드 리스트
	private List<EdgeDTO> edgeList;

	private List<String> deptList; // 참여 부서 목록

}

//
// {
// 	"id": 1,
// 	"name": "샘플 템플릿",
// 	"description": "제품 출시 전 프로세스",
// 	"nodeList": [...],   // TemplateNodeDTO[]
// 	"edgeList": [...]    // EdgeDTO[]
// 	}
